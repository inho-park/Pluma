package com.dowon.fluma.user.api;

import com.dowon.fluma.user.dto.MemberResponseDTO;
import com.dowon.fluma.user.service.MemberService;
import com.dowon.fluma.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberResource {
    private final MemberService memberService;

    @GetMapping("/getInfo")
    public ResponseEntity<MemberResponseDTO> findMemberInfoById() {
        return ResponseEntity.ok().body(memberService.findMemberInfoById(SecurityUtil.getCurrentMemberId()));
    }

    @GetMapping("/{email}")
    public ResponseEntity<MemberResponseDTO> findMemberInfoByEmail(@PathVariable String email) {
        return ResponseEntity.ok().body(memberService.findMemberInfoByUsername(email));
    }
}
