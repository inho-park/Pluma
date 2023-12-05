package com.dowon.fluma.user.service;

import com.dowon.fluma.common.service.RedisService;
import com.dowon.fluma.user.domain.Member;
import com.dowon.fluma.user.dto.MemberRequestDTO;
import com.dowon.fluma.user.dto.MemberResponseDTO;
import com.dowon.fluma.user.dto.TokenDTO;
import com.dowon.fluma.user.dto.TokenRequestDTO;
import com.dowon.fluma.common.jwt.TokenProvider;
import com.dowon.fluma.user.exception.CustomNoProviderException;
import com.dowon.fluma.user.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    final private AuthenticationManagerBuilder authenticationManagerBuilder;
    final private MemberRepository memberRepository;
    final private PasswordEncoder passwordEncoder;
    final private TokenProvider tokenProvider;
    final private RedisService redisService;

    @Transactional
    public MemberResponseDTO signup(MemberRequestDTO memberRequestDTO) {
        if (memberRepository.existsByUsername(memberRequestDTO.getUsername())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }

        Member member = memberRequestDTO.toMember(passwordEncoder);
        return MemberResponseDTO.of(memberRepository.save(member));
    }

    @Transactional
    public TokenDTO login(MemberRequestDTO memberRequestDTO) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = memberRequestDTO.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDTO tokenDTO = tokenProvider.generateTokenDTO(authentication);

        // 4. RefreshToken 저장
        redisService.setValues("RefreshToken : " + memberRequestDTO.getUsername(), tokenDTO.getRefreshToken(), Duration.ofMillis(1000 * 60 * 60 * 24 * 14L));

        // 5. 토큰 발급
        return tokenDTO;
    }

    @Transactional
    public TokenDTO reissue(TokenRequestDTO tokenRequestDTO) {
        // 1. Refresh Token 검증
        try {
            if (!tokenProvider.validateToken(tokenRequestDTO.getRefreshToken())) {
                throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
            }
        } catch (ExpiredJwtException e) {
            // 재로그인 필요하므로 컨트롤러로 예외 전달
            throw e;
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDTO.getAccessToken());
        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        String refreshToken = redisService.getValues("RefreshToken : " + authentication.getName());

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.equals(tokenRequestDTO.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDTO tokenDTO = tokenProvider.generateTokenDTO(authentication);

        // 6. 저장소 정보 업데이트
        redisService.deleteValues("RefreshToken : " + authentication.getName());
        redisService.setValues("RefreshToken : " + authentication.getName(), tokenDTO.getRefreshToken(), Duration.ofMillis(1000 * 60 * 60 * 24 * 14L));

        // 토큰 발급
        return tokenDTO;
    }
}
