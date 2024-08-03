package org.website.steez.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        log.debug("Loading user for client registration: {}", userRequest.getClientRegistration().getRegistrationId());

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        String email = (String) attributes.get("email");

        if (email == null) {
            log.debug("Email not found in attributes, fetching from GitHub");
            email = fetchEmailFromGitHub(userRequest.getAccessToken());
        }

        attributes.put("email", email);

        log.debug("Returning OAuth2User with email: {}", email);
        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "email"
        );
    }

    private String fetchEmailFromGitHub(OAuth2AccessToken accessToken) {
        log.debug("Fetching email from GitHub using access token");

        RestTemplate restTemplate = new RestTemplate();
        String uri = "https://api.github.com/user/emails";
        String tokenValue = accessToken.getTokenValue();

        try {
            List<Map<String, Object>> emailList = restTemplate.exchange(uri, HttpMethod.GET,
                    new HttpEntity<>(createHeaders(tokenValue)),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();

            for (Map<String, Object> emailEntry : emailList) {
                if (Boolean.TRUE.equals(emailEntry.get("primary"))) {
                    String email = (String) emailEntry.get("email");
                    log.debug("Primary email found: {}", email);
                    return email;
                }
            }
        } catch (Exception e) {
            log.error("Error fetching email from GitHub", e);
        }

        log.warn("No primary email found in GitHub response");
        return null;
    }

    private HttpHeaders createHeaders(String token) {
        log.debug("Creating headers for GitHub API request");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + token);
        return headers;
    }
}
