package org.website.steez.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.steez.exception.TokenRefreshException;
import org.website.steez.model.RefreshToken;
import org.website.steez.model.user.User;
import org.website.steez.repository.RefreshTokenRepository;
import org.website.steez.repository.UserRepository;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.UUID;

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

    public HttpHeaders refreshTokens(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(hashToken(token))
                .map(this::verifyExpiration)
                .orElseThrow(TokenRefreshException::new);

        User user = userRepository.findByEmail(refreshToken.getUsername())
                .orElseThrow(TokenRefreshException::new);

        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = generateRefreshToken(user);

        HttpHeaders headers = createCookieHeaders(accessToken, newRefreshToken);

        // Log the generated tokens and headers for debugging
        System.out.println("New Access Token: " + accessToken);
        System.out.println("New Refresh Token: " + newRefreshToken);
        System.out.println("Headers: " + headers);

        return headers;
    }

    public String generateRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(user.getUsername());
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

    private String convertToHex(final byte[] messageDigest) {
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
        headers.add("Set-Cookie", String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure", accessTokenName, accessToken, accessTokenDurationMs / 1000));
        headers.add("Set-Cookie", String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure", refreshTokenName, refreshToken, refreshTokenDurationMs / 1000));
        return headers;
    }

    @Transactional
    public void deleteRefreshTokenByUsername(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }
}
