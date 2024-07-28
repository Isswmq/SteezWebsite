package org.website.steez.auth;

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
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

        String email = (String) attributes.get("email");
        if (email == null) {
            email = fetchEmailFromGitHub(userRequest.getAccessToken());
        }

        attributes.put("email", email);

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "email"
        );
    }


    private String fetchEmailFromGitHub(OAuth2AccessToken accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String uri = "https://api.github.com/user/emails";
        String tokenValue = accessToken.getTokenValue();

        List<Map<String, Object>> emailList = restTemplate.exchange(uri, HttpMethod.GET,
                new HttpEntity<>(createHeaders(tokenValue)),
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();

        for (Map<String, Object> emailEntry : emailList) {
            if (Boolean.TRUE.equals(emailEntry.get("primary"))) {
                return (String) emailEntry.get("email");
            }
        }
        return null;
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + token);
        return headers;
    }
}
