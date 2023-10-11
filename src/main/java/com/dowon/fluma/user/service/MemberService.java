package com.dowon.fluma.user.service;

import com.dowon.fluma.user.dto.MemberResponseDTO;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public interface MemberService {
    MemberResponseDTO findMemberInfoById(Long memberId);
    MemberResponseDTO findMemberInfoByUsername(String username);
    void sendCodeToEmail(String email) throws NoSuchAlgorithmException;
    boolean checkDuplicatedEmail(String email);
    boolean verifiedCode(String email, String authCode);
    default String createCode() throws NoSuchAlgorithmException {
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
