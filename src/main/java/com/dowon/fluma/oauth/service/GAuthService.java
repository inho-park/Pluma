package com.dowon.fluma.oauth.service;

import com.dowon.fluma.user.repository.MemberRepository;
import com.dowon.fluma.user.service.AuthService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Log4j2
@Service
@RequiredArgsConstructor
public class GAuthService {
    final private MemberRepository memberRepository;
    final private AuthService authService;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String REDIRECT_URI;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String CLIENT_SECRET;
    private String getAccessToken(String code, String registrationId) {
        String accessToken = "";
        String refreshToken = "";
        try {
            URL url = new URL("https://oauth2.googleapis.com/token");
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

    
}
