package com.dowon.fluma.user.repo;


import com.dowon.fluma.user.domain.User;
import com.dowon.fluma.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void 유저_추가() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            userRepository.save(
                    User.builder()
                            .name("name" + i)
                            .username("username" + i)
                            .password("password" + i)
                            .build()
            );
        });
    }
}
