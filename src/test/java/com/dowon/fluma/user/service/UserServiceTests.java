package com.dowon.fluma.user.service;

import com.dowon.fluma.user.dto.MemberRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.stream.IntStream;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private AuthService authService;

    @Test
    public void 유저_추가() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            authService.signup(
                    MemberRequestDTO.builder()
                            .name("name" + i)
                            .username("username" + i)
                            .password("password" + i)
                            .build()
            );
        });
    }
}
