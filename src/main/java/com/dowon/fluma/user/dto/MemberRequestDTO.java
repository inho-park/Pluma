package com.dowon.fluma.user.dto;

import com.dowon.fluma.user.domain.Authority;
import com.dowon.fluma.user.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDTO {
    private String username;
    private String password;
    private String name;
    public Member toMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .name(name)
                .authority(Authority.ROLE_USER)
                .build();
    }
    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(username, password);
    }
}
