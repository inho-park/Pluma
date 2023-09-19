package com.dowon.fluma.user.service;

import com.dowon.fluma.user.dto.MemberResponseDTO;

public interface MemberService {
    MemberResponseDTO findMemberInfoById(Long memberId);
    MemberResponseDTO findMemberInfoByUsername(String username);
}
