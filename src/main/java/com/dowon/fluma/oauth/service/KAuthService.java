package com.dowon.fluma.oauth.service;

import com.dowon.fluma.common.jwt.TokenProvider;
import com.dowon.fluma.common.service.RedisService;
import com.dowon.fluma.user.domain.Authority;
import com.dowon.fluma.user.domain.Member;
import com.dowon.fluma.user.dto.TokenDTO;
import com.dowon.fluma.user.repository.MemberRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class KAuthService {
    final private MemberRepository memberRepository;
    final private TokenProvider tokenProvider;
    final private RedisService redisService;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    public String getToken(String code) {
        String accessToken = "";
        String refreshToken = "";
        try {
            URL url = new URL("https://kauth.kakao.com/oauth/token");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
            String query = "grant_type=authorization_code" +
                    "&client_id=" + clientId +
                    "&client_secret=" + clientSecret +
                    "&redirect_uri=" + redirectUri +
                    "&code=" + code;
            bw.write(query);
            bw.flush();

            log.info("[KAUTH Service getToken] response code : " + httpURLConnection.getResponseCode());

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line, result = "";
            while((line = br.readLine()) != null) {
                result += line;
            }
            log.info("[KAUTH Service getToken] response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();


            bw.close();
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return accessToken;
    }

    public TokenDTO getKakaoUser(String accessToken) throws MalformedURLException{
        String requestURL = "https://kapi.kakao.com/v2/user/me";
        TokenDTO tokenDTO = null;
        try {
            URL url = new URL(requestURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);

            log.info("[KAUTH Service getKakaoUser] response code : " + httpURLConnection.getResponseCode());

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            String result = "";
            while((line = br.readLine()) != null) {
                result += line;
            }
            log.info("[KAUTH Service getKakaoUser] response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            Long id = element.getAsJsonObject().get("id").getAsLong();
            String name = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();
            String email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            String provider = "kakao";
            String providerId = provider + "-" + id;
            log.info("[KAUTH Service getKakaoUser] id : " + id + ", name : " + name + ", email : " + email);

            Optional<Member> optionalUser = memberRepository.findByUsername(email);
            Member member = null;

            if(optionalUser.isEmpty()) {
                member = Member.builder()
                        .name(name)
                        .username(email)
                        .provider(provider)
                        .providerId(providerId)
                        .authority(Authority.ROLE_USER)
                        .build();
                memberRepository.save(member);
            } else {
                member = optionalUser.get();
            }
            tokenDTO = tokenProvider.getTokens(member.getName(), member.getAuthority().name());
            redisService.setValues("RefreshToken : " + member.getUsername(), tokenDTO.getRefreshToken(), Duration.ofMillis(1000 * 60 * 60 * 24 * 14L));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokenDTO;
    }
}
