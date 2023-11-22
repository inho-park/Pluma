package com.dowon.fluma.oauth.service;

import com.dowon.fluma.oauth.dto.GoogleUserInfo;
import com.dowon.fluma.oauth.dto.KakaoUserInfo;
import com.dowon.fluma.oauth.dto.NaverUserInfo;
import com.dowon.fluma.oauth.dto.OAuth2UserInfo;
import com.dowon.fluma.user.domain.Authority;
import com.dowon.fluma.user.domain.Member;
import com.dowon.fluma.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = null;

        String provider = userRequest.getClientRegistration().getRegistrationId();

        if(provider.equals("google")) {
            log.info("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo( oAuth2User.getAttributes() );
        } else if(provider.equals("kakao")) {
            log.info("카카오 로그인 요청");
            oAuth2UserInfo = new KakaoUserInfo( (Map<String, Object>) oAuth2User.getAttributes() );
        } else if(provider.equals("naver")) {
            log.info("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo( (Map<String, Object>) oAuth2User.getAttributes().get("response") );
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String loginId = provider + "_" + providerId;
        String nickname = oAuth2UserInfo.getName();


        Optional<Member> optionalUser = memberRepository.findByUsername(loginId);
        Member member = null;

        if(optionalUser.isEmpty()) {
            member = Member.builder()
                    .name(nickname)
                    .provider(provider)
                    .providerId(providerId)
                    .authority(Authority.ROLE_USER)
                    .build();
            memberRepository.save(member);
        } else {
            member = optionalUser.get();
        }

        return member;
    }
}
