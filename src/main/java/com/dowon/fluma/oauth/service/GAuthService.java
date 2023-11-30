package com.dowon.fluma.oauth.service;

import com.dowon.fluma.user.domain.Authority;
import com.dowon.fluma.user.domain.Member;
import com.dowon.fluma.user.dto.MemberRequestDTO;
import com.dowon.fluma.user.dto.TokenDTO;
import com.dowon.fluma.user.repository.MemberRepository;
import com.dowon.fluma.user.service.AuthService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class GAuthService {
    final private MemberRepository memberRepository;
    final private AuthService authService;
    final private PasswordEncoder passwordEncoder;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String REDIRECT_URI;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String CLIENT_SECRET;
    public String getToken(String code) {
        String accessToken = "";
        String refreshToken = "";
        try {
            String tokenRequestURL = "https://oauth2.googleapis.com/token";
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> params = new HashMap<>();
            params.put("code", code);
            params.put("client_id", CLIENT_ID);
            params.put("client_secret", CLIENT_SECRET);
            params.put("redirect_uri", REDIRECT_URI);
            params.put("grant_type", "authorization_code");

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenRequestURL, params, String.class);

            log.info("[GAUTH Service getToken] response body : " + responseEntity.getStatusCode());
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
        Member member = null;
        String userInfoRequestURL = "https://www.googleapis.com/oauth2/v1/userinfo";
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(userInfoRequestURL, HttpMethod.GET, request, String.class);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response.getBody());

        Long id = element.getAsJsonObject().get("id").getAsLong();
        String name = element.getAsJsonObject().get("nickname").getAsString();
        String email = element.getAsJsonObject().get("email").getAsString();
        String provider = "google";
        String providerId = provider + "-" + id;
        log.info("[GAUTH Service getKakaoUser] id : " + id + ", name : " + name + ", email : " + email);

        Optional<Member> optionalUser = memberRepository.findByUsername(email);

        if(optionalUser.isEmpty()) {
            member = memberRepository.save(Member.builder()
                    .name(name)
                    .username(email)
                    // 임시 방편으로 현재 password 를 임의로 부여하여 로그인시킨 상태
                    // => password 없이 로그인 로직을 강제할 수 있는 기능 필요
                    .password(passwordEncoder.encode(providerId))
                    .provider(provider)
                    .providerId(providerId)
                    .authority(Authority.ROLE_USER)
                    .build());
        } else {
            member = optionalUser.get();
        }

        return authService.login(MemberRequestDTO.builder()
                .username(member.getUsername())
                .name(member.getName())
                .password(providerId)
                .build()
        );

    }
}
