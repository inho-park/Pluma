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
    final private OAuthService oAuthService;
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

    public TokenDTO getKakaoUserInfo(String accessToken) throws MalformedURLException {
        String requestURL = "https://kapi.kakao.com/v2/user/me";
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
            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.info("[KAUTH Service getKakaoUserInfo] response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            String id = element.getAsJsonObject().get("id").getAsString();
            String name = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();
            String email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            String provider = "kakao";
            log.info("[KAUTH Service getKakaoUserInfo] id : " + id + ", name : " + name + ", email : " + email);
            return oAuthService.makeTokenDTO(name, email, provider, id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
