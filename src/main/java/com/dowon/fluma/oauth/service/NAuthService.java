package com.dowon.fluma.oauth.service;

import com.dowon.fluma.user.domain.Authority;
import com.dowon.fluma.user.domain.Member;
import com.dowon.fluma.user.dto.MemberRequestDTO;
import com.dowon.fluma.user.dto.TokenDTO;
import com.dowon.fluma.user.repository.MemberRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class NAuthService {
    final private OAuthService oAuthService;
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String REDIRECT_URI;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String CLIENT_SECRET;

    public String getToken(String code, String state) {
        String accessToken = "";
        try {
            String tokenRequestURL = "https://nid.naver.com/oauth2.0/token";
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("state", state);
            params.add("code", code);
            params.add("client_id", CLIENT_ID);
            params.add("client_secret", CLIENT_SECRET);
            params.add("redirect_uri", REDIRECT_URI);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<?> request = new HttpEntity<>(params, httpHeaders);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenRequestURL, request, String.class);

            log.info("[NAUTH Service getToken] response status : " + responseEntity.getStatusCode());
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

    public TokenDTO getNaverUserInfo(String accessToken) {
        Member member = null;
        String userInfoRequestURL = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(userInfoRequestURL, HttpMethod.GET, request, String.class);
        log.info("[NAUTH Service getNaverUserInfo] response code : " + response.getStatusCode());
        log.info("[NAUTH Service getNaverUserInfo] response body : " + response.getBody());
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response.getBody()).getAsJsonObject().get("response");

        String id = element.getAsJsonObject().get("id").getAsString();
        String name = element.getAsJsonObject().get("name").getAsString();
        String email = element.getAsJsonObject().get("email").getAsString();
        String provider = "naver";
        String providerId = provider + "-" + id;
        log.info("[NAUTH Service getNaverUserInfo] id : " + id + ", name : " + name + ", email : " + email);

        return oAuthService.makeTokenDTO(name, email, provider, providerId);
    }
}
