package com.dowon.fluma.oauth.controller;

import com.dowon.fluma.oauth.service.GAuthService;
import com.dowon.fluma.oauth.service.KAuthService;
import com.dowon.fluma.oauth.service.NAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthResource {
    final private KAuthService kAuthService;
    final private GAuthService gAuthService;
    final private NAuthService nAuthService;
    @GetMapping("/kakao")
    public ResponseEntity kakaoCallback(@RequestParam String code) {
        try {
            String accessToken = kAuthService.getToken(code);
            log.info("[KAUTH controller] " + accessToken);
            return ResponseEntity.ok().body(kAuthService.getKakaoUserInfo(accessToken));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/google")
    public ResponseEntity googleCallback(@RequestParam String code) {
        try {
            String accessToken = gAuthService.getToken(code);
            return ResponseEntity.ok().body(gAuthService.getGoogleUserInfo(accessToken));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/naver")
    public ResponseEntity naverCallback(@RequestParam String code, @RequestParam String state) {
        try {
            String accessToken = nAuthService.getToken(code, state);
            return ResponseEntity.ok().body(nAuthService.getNaverUserInfo(accessToken));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
