package org.website.steez.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.steez.exception.TokenRefreshException;
import org.website.steez.model.RefreshToken;
import org.website.steez.repository.RefreshTokenRepository;
import org.website.steez.repository.UserRepository;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${refresh_token_duration}")
    private long refreshTokenDurationMs;

    @Value("${access_token_duration}")
    private long accessTokenDurationMs;

    @Value("${access_token_name}")
    private String accessTokenName;

    @Value("${refresh_token_name}")
    private String refreshTokenName;

    @Value("${access_token_presence_name}")
    private String accessTokenPresenceName;

    @Value("${refresh_token_presence_name}")
    private String refreshTokenPresenceName;
    
    public HttpHeaders refreshTokens(String token) {
        return refreshTokenRepository.findByToken(hashToken(token))
                .map(this::verifyExpiration)
                .map(RefreshToken::getUsername)
                .map(String::toLowerCase)
                .map(userRepository::findByEmail)
                .orElseThrow(TokenRefreshException::new)
                .map(user -> {
                    String accessToken = jwtService.generateAccessToken(user);
                    String refreshToken = generateRefreshToken(user);

                    return createCookieHeaders(accessToken, refreshToken);
                })
                .orElseThrow(TokenRefreshException::new);
    }
    
    public String generateRefreshToken(UserDetails userDetails) {
        RefreshToken refreshToken = new RefreshToken();
        String username = userDetails.getUsername();
        
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        String token = UUID.randomUUID().toString();
        
        refreshToken.setToken(hashToken(token));
        
        refreshTokenRepository.save(refreshToken);
        return token;
    }
    
    private String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return convertToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new TokenRefreshException();
        }
    }
    
    private String convertToHex(final byte[] messageDigest){
        BigInteger bigint = new BigInteger(1, messageDigest);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(bigint.toString(16));
        if (stringBuilder.length() < 32) {
            stringBuilder.append("0".repeat(32 - stringBuilder.length()));
        }
        return stringBuilder.toString();
    }
    
    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException();
        }
        return token;
    }

    private HttpHeaders createCookieHeaders(String accessToken, String refreshToken) {
        HttpHeaders headers = new HttpHeaders();

        Stream.of(
                String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure", accessTokenName, accessToken, accessTokenDurationMs / 1000),
                String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure", refreshTokenName, refreshToken, refreshTokenDurationMs / 1000),
                String.format("%s=; Max-Age=%d; Path=/; Secure;", accessTokenPresenceName, accessTokenDurationMs / 1000),
                String.format("%s=; Max-Age=%d; Path=/; Secure;", refreshTokenPresenceName, refreshTokenDurationMs / 1000)
        ).forEach(s -> headers.add("Set-Cookie", s));

        return headers;
    }

    @Transactional
    public void deleteRefreshTokenByUsername(String username){
        refreshTokenRepository.deleteByUsername(username);
    }
}
