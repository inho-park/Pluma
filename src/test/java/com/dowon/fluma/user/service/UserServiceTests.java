package com.dowon.fluma.user.service;

import com.dowon.fluma.user.domain.Role;
import com.dowon.fluma.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.stream.IntStream;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @Test
    public void 유저_추가() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            userService.saveUser(new User(
                    null,
                    "username" + i,
                    "password" + i,
                    "name" + i,
                    new ArrayList<>())
            );
        });
    }

    @Test
    public void 롤_추가() {
        userService.saveRole(new Role(null, "ROLE_USER"));
//        userService.saveRole(new Role(null, "ROLE_MANAGER"));
    }

    @Test
    public void 유저_롤_추가() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
//            User user = userService.getUser("username" + i);
//            if (user.isCounselor()) userService.addRoleToUser("username" + i,"ROLE_MANAGER");
            userService.addRoleToUser("username" + i,"ROLE_USER");
        });
    }
}
