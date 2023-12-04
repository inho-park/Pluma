package com.dowon.fluma.oauth.service;

import com.dowon.fluma.user.domain.Member;
import com.dowon.fluma.user.dto.TokenDTO;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class GAuthService {
    final private OAuthService oAuthService;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String REDIRECT_URI;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String CLIENT_SECRET;
    public String getToken(String code) {
        String accessToken = "";
        try {
            String tokenRequestURL = "https://oauth2.googleapis.com/token";
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> params = new HashMap<>();
            String authorizationCode = "authorization_code";
            params.put("code", code);
            params.put("client_id", CLIENT_ID);
            params.put("client_secret", CLIENT_SECRET);
            params.put("redirect_uri", REDIRECT_URI);
            params.put("grant_type", authorizationCode);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenRequestURL, params, String.class);

            log.info("[GAUTH Service getToken] response status : " + responseEntity.getStatusCode());
            log.info(responseEntity.getBody());
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(responseEntity.getBody());

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            log.info(accessToken);


        } catch(Exception e) {
            e.printStackTrace();
        }

        return accessToken;
    }

    public TokenDTO getGoogleUserInfo(String accessToken) {
        String userInfoRequestURL = "https://www.googleapis.com/oauth2/v1/userinfo";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(userInfoRequestURL, HttpMethod.GET, request, String.class);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response.getBody());

        String id = element.getAsJsonObject().get("id").getAsString();
        String name = element.getAsJsonObject().get("name").getAsString();
        String email = element.getAsJsonObject().get("email").getAsString();
        String provider = "google";
        String providerId = provider + "-" + id;
        log.info("[GAUTH Service getKakaoUser] id : " + id + ", name : " + name + ", email : " + email);


        return oAuthService.makeTokenDTO(name, email, provider, providerId);
    }
}
