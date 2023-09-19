package com.dowon.fluma.user.dto;

import com.dowon.fluma.user.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDTO {
    private String name;
    private Long userId;

    public static MemberResponseDTO of(Member member) {
        return new MemberResponseDTO(member.getName(), member.getId());
    }
}
