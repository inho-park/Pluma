package com.dowon.fluma.oauth.service;

import com.dowon.fluma.user.domain.Authority;
import com.dowon.fluma.user.domain.Member;
import com.dowon.fluma.user.dto.MemberRequestDTO;
import com.dowon.fluma.user.dto.TokenDTO;
import com.dowon.fluma.user.repository.MemberRepository;
import com.dowon.fluma.user.service.AuthService;
import com.google.gson.JsonElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class OAuthService {
    final private MemberRepository memberRepository;
    final private PasswordEncoder passwordEncoder;
    final private AuthService authService;
    public JsonElement getTokenResponse(String url, String accessToken) {

        return null;
    }

    public TokenDTO makeTokenDTO(String name, String email,String provider, String providerId) {
        Member member = null;
        Optional<Member> optionalUser = memberRepository.findByUsernameAndProviderId(email, providerId);

        if(optionalUser.isEmpty()) {
            member = memberRepository.save(Member.builder()
                    .name(name)
                    .username(email)
                    // 임시 방편으로 현재 password 를 임의로 부여하여 로그인시킨 상태
                    // => password 없이 로그인 로직을 강제할 수 있는 기능 필요
                    .password(passwordEncoder.encode("oauth client"))
                    .provider(provider)
                    .providerId(providerId)
                    .authority(Authority.ROLE_USER)
                    .build());
        } else {
            member = optionalUser.get();
        }

        return authService.login(MemberRequestDTO.builder()
                .username(member.getUsername())
                .name(member.getName())
                .password("oauth client")
                .build()
        );
    }
}
