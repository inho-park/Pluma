package com.dowon.fluma.oauth.controller;

import com.dowon.fluma.oauth.service.KAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class KAuthController {
    final private KAuthService kAuthService;
    @GetMapping("/kakao")
    public void kakaoCallback(@RequestParam String code) {
        try {
            String accessToken = kAuthService.getToken(code);
            log.info("[KAUTH controller] " + accessToken);
            kAuthService.getKakaoUser(accessToken);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
