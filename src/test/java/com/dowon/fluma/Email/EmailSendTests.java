package com.dowon.fluma.Email;

import com.dowon.fluma.common.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailSendTests {
    @Autowired
    private MailService mailService;

    @Test
    public void 이메일_보내기() {

    }
}
