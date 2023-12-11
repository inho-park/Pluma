package com.dowon.fluma.document.service;

import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class TranslateService {
    @Value("${DEEPL.auth-key}")
    private String authKey;
    private Translator translator;
    public String translateEngToKor(String korean) {
        try {
            translator = new Translator(authKey);
            TextResult result = translator.translateText(korean, "en", "ko");
            log.info("translated data : " + result.getText());
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
