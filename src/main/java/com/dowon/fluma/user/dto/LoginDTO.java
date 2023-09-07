package com.dowon.fluma.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDTO {
    private String username;
    private String password;
}
