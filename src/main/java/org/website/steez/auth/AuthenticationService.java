package org.website.steez.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.website.steez.exception.TokenRefreshException;
import org.website.steez.exception.UserWithEmailAlreadyExistException;
import org.website.steez.model.Role;
import org.website.steez.model.User;
import org.website.steez.repository.UserRepository;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;
    
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
    
    public HttpHeaders register(RegisterRequest request) {
        User userToBeRegistered = User.builder()
                .username(request.getUsername())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        
        if(userRepository.findByEmail(userToBeRegistered.getEmail().toLowerCase()).isPresent()) {
            throw new UserWithEmailAlreadyExistException();
        }
        
        User savedUser = userRepository.save(userToBeRegistered);
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = refreshTokenService.generateRefreshToken(savedUser);
        
        return createCookieHeaders(accessToken, refreshToken);
    }
    
    public HttpHeaders authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword())
        );
        User loggedUser = userRepository
                .findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(loggedUser);
        String refreshToken = refreshTokenService.generateRefreshToken(loggedUser);

        return createCookieHeaders(accessToken, refreshToken);
    }
    
    public HttpHeaders refresh(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            throw new TokenRefreshException();
        }
        
        Cookie requestRefreshToken = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(refreshTokenName))
                .findAny()
                .orElseThrow(TokenRefreshException::new);
        
        return refreshTokenService.refreshTokens(requestRefreshToken.getValue());
    }
    
    public HttpHeaders logout(HttpServletRequest request) {
        
        HttpHeaders logoutCookieHeaders = createLogoutCookieHeaders();
        
        if (request.getCookies() == null || request.getCookies().length == 0) {
            return logoutCookieHeaders;
        }
        
        Optional<Cookie> accessCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(accessTokenName))
                .findAny();
        
        if (accessCookie.isEmpty()) {
            return logoutCookieHeaders;
        }

        String accessToken = accessCookie.get().getValue();
        String userEmail = jwtService.extractUsername(accessToken);
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (!jwtService.isTokenValid(accessToken, userDetails)) {
            return logoutCookieHeaders;
        }

        refreshTokenService.deleteRefreshTokenByUsername(userEmail);
        return logoutCookieHeaders;
    }

    private HttpHeaders createCookieHeaders(String accessToken, String refreshToken, long accessTokenDuration, long refreshTokenDuration) {
        HttpHeaders headers = new HttpHeaders();

        Stream.of(
                String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure", accessTokenName, accessToken, accessTokenDuration),
                String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure", refreshTokenName, refreshToken, refreshTokenDuration),
                String.format("%s=; Max-Age=%d; Path=/; Secure;", accessTokenPresenceName, accessTokenDuration),
                String.format("%s=; Max-Age=%d; Path=/; Secure;", refreshTokenPresenceName, refreshTokenDuration)
        ).forEach(s -> headers.add("Set-Cookie", s));

        return headers;
    }

    private HttpHeaders createLogoutCookieHeaders() {
        return createCookieHeaders(null, null, 0, 0);
    }

    private HttpHeaders createCookieHeaders(String accessToken, String refreshToken) {
        return createCookieHeaders(accessToken, refreshToken, accessTokenDurationMs / 1000, refreshTokenDurationMs / 1000);
    }
    
}
