package org.website.steez.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.website.steez.model.user.Role;
import org.website.steez.model.user.User;
import org.website.steez.repository.UserRepository;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;
    private final CookieUtil cookieUtil;
    
    @Value("${refresh_token_duration}")
    private long refreshTokenDurationMs;
    
    @Value("${access_token_duration}")
    private long accessTokenDurationMs;
    
    @Value("${access_token_name}")
    private String accessTokenName;
    
    @Value("${refresh_token_name}")
    private String refreshTokenName;
    
    public HttpHeaders register(RegisterRequest request) {
        log.debug("Registering user with email: {}", request.getEmail());

        User userToBeRegistered = User.builder()
                .username(request.getUsername())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountNonLocked(true)
                .role(Role.USER)
                .build();
        
        if(userRepository.findByEmail(userToBeRegistered.getEmail().toLowerCase()).isPresent()) {
            log.warn("User with email {} already exists", request.getEmail());
            throw new UserWithEmailAlreadyExistException();
        }
        
        User savedUser = userRepository.save(userToBeRegistered);
        log.debug("User with email {} registered successfully", request.getEmail());
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = refreshTokenService.generateRefreshToken(savedUser);

        log.debug("Generated tokens for user with email: {}", request.getEmail());
        return createCookieHeaders(accessToken, refreshToken);
    }
    
    public HttpHeaders authenticate(AuthenticationRequest request) {
        log.debug("Authenticating user with email: {}", request.getEmail());
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

        log.debug("Generated tokens for authenticated user with email: {}", request.getEmail());
        return createCookieHeaders(accessToken, refreshToken);
    }
    
    public HttpHeaders refresh(HttpServletRequest request) {
        log.debug("Refreshing tokens");
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            log.warn("No cookies found for token refresh");
            throw new TokenRefreshException();
        }
        
        Cookie requestRefreshToken = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(refreshTokenName))
                .findAny()
                .orElseThrow(TokenRefreshException::new);

        log.debug("Refresh token found, refreshing tokens");
        return refreshTokenService.refreshTokens(requestRefreshToken.getValue());
    }
    
    public HttpHeaders logout(HttpServletRequest request) {
        log.debug("Logging out user");
        HttpHeaders logoutCookieHeaders = createLogoutCookieHeaders();
        
        if (request.getCookies() == null || request.getCookies().length == 0) {
            log.debug("No cookies found for logout");
            return logoutCookieHeaders;
        }
        
        Optional<Cookie> accessCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(accessTokenName))
                .findAny();
        
        if (accessCookie.isEmpty()) {
            log.debug("No access token cookie found for logout");
            return logoutCookieHeaders;
        }

        String accessToken = accessCookie.get().getValue();
        String userEmail = jwtService.extractUsername(accessToken);
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (!jwtService.isTokenValid(accessToken, userDetails)) {
            log.warn("Invalid access token for user: {}", userEmail);
            return logoutCookieHeaders;
        }

        refreshTokenService.deleteRefreshTokenByUsername(userEmail);
        log.debug("User {} logged out successfully", userEmail);
        return logoutCookieHeaders;
    }

    private HttpHeaders createLogoutCookieHeaders() {
        return cookieUtil.createCookieHeaders(null, null, 0, 0);
    }

    private HttpHeaders createCookieHeaders(String accessToken, String refreshToken) {
        return cookieUtil.createCookieHeaders(accessToken, refreshToken, accessTokenDurationMs / 1000, refreshTokenDurationMs / 1000);
    }
}
