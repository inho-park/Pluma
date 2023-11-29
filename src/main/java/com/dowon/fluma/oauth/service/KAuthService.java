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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class KAuthService {
    final private MemberRepository memberRepository;
    final private AuthService authService;
    final private PasswordEncoder passwordEncoder;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URI;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String CLIENT_SECRET;

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
                    "&client_id=" + CLIENT_ID +
                    "&client_secret=" + CLIENT_SECRET +
                    "&redirect_uri=" + REDIRECT_URI +
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
        Member member = null;
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
            
            TokenDTO tokenDTO = authService.login(MemberRequestDTO.builder()
                    .username(member.getUsername())
                    .name(member.getName())
                    .password(providerId)
                    .build()
            );
            log.info("[KAUTH Service getKakaoUser] access token : " + tokenDTO.getAccessToken() + ", refresh token : " + tokenDTO.getRefreshToken());
            return tokenDTO;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
