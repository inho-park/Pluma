package com.dowon.fluma.document.api;

import com.dowon.fluma.document.service.TranslateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
public class TranslateResource {
    private final TranslateService translateService;

    @GetMapping(value = "/trans/")
    public ResponseEntity getEnglish(@RequestParam(value = "kor") String korean) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("eng", translateService.translateEngToKor(korean));
            return new ResponseEntity(map, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
