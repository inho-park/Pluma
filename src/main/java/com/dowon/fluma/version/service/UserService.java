package com.dowon.fluma.version.service;





import com.dowon.fluma.user.domain.Role;
import com.dowon.fluma.user.domain.User;
import com.dowon.fluma.user.dto.LoginDTO;

import java.util.List;

public interface UserService {
    LoginDTO saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    User getUser(String username);
    List<User> getUsers();
}
