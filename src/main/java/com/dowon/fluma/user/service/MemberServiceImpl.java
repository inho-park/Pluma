package com.dowon.fluma.user.service;

import com.dowon.fluma.common.service.MailService;
import com.dowon.fluma.common.service.RedisService;
import com.dowon.fluma.user.dto.MemberResponseDTO;
import com.dowon.fluma.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{
    final private MemberRepository memberRepository;
    final private MailService mailService;
    final private RedisService redisService;
    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpiration;

    @Override
    public MemberResponseDTO findMemberInfoById(Long memberId) {
        return memberRepository.findById(memberId).
                map(MemberResponseDTO::of)
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다"));
    }

    @Override
    public MemberResponseDTO findMemberInfoByUsername(String username) {
        return memberRepository.findByUsername(username).
                map(MemberResponseDTO::of)
                .orElseThrow(() -> new RuntimeException("유저 정보가 없습니다"));
    }

    @Override
    public void sendCodeToEmail(String email) throws NoSuchAlgorithmException {
        if (this.checkDuplicatedEmail(email)) {
            throw new RuntimeException("이미 등록된 이메일");
        }
        else {
            String title = "Travel with me 이메일 인증 번호";
            String code = createCode();
            mailService.sendEmail(email, title, "인증 번호 : " + code);
            redisService.setValues(email, code, Duration.ofMillis(authCodeExpiration));
        }
    }

    @Override
    public boolean checkDuplicatedEmail(String email) {
        return memberRepository.findByUsername(email).isPresent();
    }

    @Override
    public boolean verifiedCode(String email, String authCode) {
        try {
            String code = redisService.getValues(email);
            return redisService.checkExistsValue(code) && authCode.equals(code);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
