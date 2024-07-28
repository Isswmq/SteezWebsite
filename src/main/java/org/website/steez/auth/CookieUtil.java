package org.website.steez.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class CookieUtil {

    @Value("${access_token_name}")
    private String accessTokenName;

    @Value("${refresh_token_name}")
    private String refreshTokenName;

    @Value("${access_token_presence_name}")
    private String accessTokenPresenceName;

    @Value("${refresh_token_presence_name}")
    private String refreshTokenPresenceName;

    protected HttpHeaders createCookieHeaders(String accessToken, String refreshToken, long accessTokenDuration, long refreshTokenDuration) {
        HttpHeaders headers = new HttpHeaders();

        Stream.of(
                String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure", accessTokenName, accessToken, accessTokenDuration),
                String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure", refreshTokenName, refreshToken, refreshTokenDuration),
                String.format("%s=; Max-Age=%d; Path=/; Secure;", accessTokenPresenceName, accessTokenDuration),
                String.format("%s=; Max-Age=%d; Path=/; Secure;", refreshTokenPresenceName, refreshTokenDuration)
        ).forEach(s -> headers.add("Set-Cookie", s));

        return headers;
    }
}
