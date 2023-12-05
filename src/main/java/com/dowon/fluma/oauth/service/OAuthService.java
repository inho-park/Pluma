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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class OAuthService {
    final private MemberRepository memberRepository;
    final private PasswordEncoder passwordEncoder;
    final private AuthService authService;

    /**
     * 내가 원하는 json 객체의 회원 정보만을 가져와도 변수 명이 달라 추상화 불가능
     * @param url
     * @param accessToken
     * @return
     */
    public JsonElement getTokenResponse(String url, String accessToken) {
        return null;
    }

    public TokenDTO makeTokenDTO(String name, String email, String provider, String providerId) {
        Member member = null;
        email += " " + provider;
        Optional<Member> optionalUser = memberRepository.findByUsernameAndProviderId(email, providerId);

        if(optionalUser.isEmpty()) {
            // provider 와 providerId 컬럼을 없애고 username 에 두 정보를 합치는 방식으로 가고자함
            member = memberRepository.save(Member.builder()
                    .name(name)
                    .username(email)
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
