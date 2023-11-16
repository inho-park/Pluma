package com.dowon.fluma.oauth.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Log4j2
@Service
public class KAuthService {
    @Value("${KAKAO.OAUTH_KEY}")
    private String clientId;

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
                    "&redirect_uri=http://localhost:8080/oauth/kakao" +
                    "&code=" + code;
            bw.write(query);
            bw.flush();

            log.info("[KAUTH Service] response code : " + httpURLConnection.getResponseCode());

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line, result = "";
            while((line = br.readLine()) != null) {
                result += line;
            }
            log.info("[KAUTH Service] response body : " + result);

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
