package com.dowon.fluma.user.api;

import com.dowon.fluma.user.dto.MemberRequestDTO;
import com.dowon.fluma.user.dto.MemberResponseDTO;
import com.dowon.fluma.user.dto.TokenDTO;
import com.dowon.fluma.user.dto.TokenRequestDTO;
import com.dowon.fluma.user.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthResource {
    private final AuthService authService;
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDTO> signup(@RequestBody MemberRequestDTO memberRequestDTO) {
        return ResponseEntity.ok(authService.signup(memberRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody MemberRequestDTO memberRequestDTO) {
        return ResponseEntity.ok(authService.login(memberRequestDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity reissue(@RequestBody TokenRequestDTO tokenRequestDTO) {
        try {
            return ResponseEntity.ok(authService.reissue(tokenRequestDTO));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.ok("Refresh 기한 만료, 로그인 필요");
        }
    }
}
