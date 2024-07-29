package org.website.steez.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.website.steez.exception.TokenRefreshException;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.util.ObjectUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Value("${access_token_name}")
    private String accessTokenName;

    @Value("${refresh_token_name}")
    private String refreshTokenName;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("JwtAuthenticationFilter is processing request...");

        if (request.getCookies() == null || request.getCookies().length == 0) {
            filterChain.doFilter(request, response);
            return;
        }

        final Optional<Cookie> accessCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> Objects.equals(cookie.getName(), accessTokenName))
                .findFirst();

        if (accessCookie.isEmpty()) {
            System.out.println("No access token cookie found");
            // Check for refresh token and handle refresh
            final Optional<Cookie> refreshCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> Objects.equals(cookie.getName(), refreshTokenName))
                    .findFirst();

            if (refreshCookie.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            final String refreshToken = refreshCookie.get().getValue();

            try {
                HttpHeaders newHeaders = refreshTokenService.refreshTokens(refreshToken);

                newHeaders.forEach((key, values) -> values.forEach(value -> response.addHeader(key, value)));

                // Extract new access token from headers
                String newAccessToken = newHeaders.getFirst(accessTokenName);
                if (newAccessToken == null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // Set new access token in SecurityContext
                String userEmail = jwtService.extractUsername(newAccessToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (TokenRefreshException ex) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid refresh token");
                return;
            }
        } else {
            final String accessToken = accessCookie.get().getValue();
            if (isEmpty(accessToken)) {
                filterChain.doFilter(request, response);
                return;
            }

            String userEmail = jwtService.extractUsername(accessToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (!jwtService.isTokenValid(accessToken, userDetails)) {
                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}

