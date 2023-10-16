package com.dowon.fluma.Email;

import com.dowon.fluma.common.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@SpringBootTest
public class EmailSendTests {
    @Autowired
    private MailService mailService;

    @Test
    public void 이메일_보내기() {
        Random random = null;
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            builder.append(random.nextInt(10));
        }
        mailService.sendEmail("이메일 입력",
                "이메일 인증하기 참 쉽죠잉",
                "인증번호 : " + builder.toString());
    }
}
