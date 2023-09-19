package com.dowon.fluma.user.service;

import com.dowon.fluma.user.dto.MemberResponseDTO;
import com.dowon.fluma.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

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
}
