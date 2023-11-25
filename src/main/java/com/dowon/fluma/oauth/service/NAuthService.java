package com.dowon.fluma.oauth.service;

import com.dowon.fluma.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class NAuthService {
    final private MemberRepository memberRepository;
}
