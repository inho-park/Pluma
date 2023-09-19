package com.dowon.fluma.user.service;

import com.dowon.fluma.user.dto.MemberRequestDTO;
import com.dowon.fluma.user.dto.MemberResponseDTO;
import com.dowon.fluma.user.dto.TokenDTO;
import com.dowon.fluma.user.dto.TokenRequestDTO;

public interface AuthService {
    public MemberResponseDTO signup(MemberRequestDTO memberRequestDTO);
    public TokenDTO login(MemberRequestDTO memberRequestDTO);
    public TokenDTO reissue(TokenRequestDTO tokenRequestDTO);
}
