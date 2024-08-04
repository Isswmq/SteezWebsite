package org.website.steez.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.website.steez.dto.user.UserCreateEditDto;
import org.website.steez.model.user.User;
import org.website.steez.repository.RefreshTokenRepository;
import org.website.steez.repository.UserRepository;
import org.website.steez.service.UserService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2Service {

    private final UserService userService;
    private final UserRepository userRepository;
    private final CookieUtil cookieUtil;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${refresh_token_duration}")
    private long refreshTokenDurationMs;

    @Value("${access_token_duration}")
    private long accessTokenDurationMs;

    public void handleOAuth2Login(OidcUser oidcUser, HttpServletResponse response) throws IOException {
        String email = oidcUser.getEmail();
        log.debug("Handling OAuth2 login for OIDC user with email: {}", email);
        processOAuth2Login(email, response);
    }

    public void handleOAuth2Login(DefaultOAuth2User defaultOAuth2User, HttpServletResponse response) throws IOException {
        String email = defaultOAuth2User.getAttribute("email");
        log.debug("Handling OAuth2 login for OAuth2 user with email: {}", email);
        processOAuth2Login(email, response);
    }

    private void processOAuth2Login(String email, HttpServletResponse response) throws IOException {
        log.debug("Processing OAuth2 login for email: {}", email);

        User user = userService.findByEmail(email)
                .orElseGet(() -> {
                    log.debug("User not found, creating new user with email: {}", email);
                    UserCreateEditDto userCreateEditDto = UserCreateEditDto.builder()
                            .email(email)
                            .rawPassword(null)
                            .build();
                    return userService.create(userCreateEditDto);
                });

        log.debug("Saving user: {}", user.getEmail());
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        log.debug("Generated access token for user: {}", user.getEmail());

        String refreshToken = refreshTokenService.generateRefreshToken(user);
        log.debug("Generated refresh token for user: {}", user.getEmail());

        HttpHeaders headers = cookieUtil.createCookieHeaders(accessToken, refreshToken, accessTokenDurationMs, refreshTokenDurationMs);
        log.debug("Created cookie headers for user: {}", user.getEmail());

        headers.forEach((key, values) -> values.forEach(value -> response.addHeader(key, value)));

        log.debug("Redirecting user: {} to /api/v1/user/cabinet", user.getEmail());
        response.sendRedirect("/api/v1/user/cabinet");
    }
}

