package com.dowon.fluma.user.api;

import com.dowon.fluma.user.dto.*;
import com.dowon.fluma.user.service.AuthService;
import com.dowon.fluma.user.service.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthResource {
    final private AuthService authService;
    final private MemberService memberService;
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
            return ResponseEntity.badRequest().body("Refresh Token Period expires");
        }
    }

    @PostMapping(value = "/emails/verification")
    public ResponseEntity sendMessage(@RequestBody EmailDTO emailDTO) {
        try {
            memberService.sendCodeToEmail(emailDTO.getEmail());
            return new ResponseEntity("success", HttpStatus.OK);
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity("fail", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/emails-code/verification")
    public ResponseEntity verificationEmail(@RequestBody EmailDTO emailDTO) {
        try {
            return new ResponseEntity(memberService.verifiedCode(emailDTO.getEmail(), emailDTO.getCode()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(false, HttpStatus.BAD_REQUEST);
        }
    }
}
