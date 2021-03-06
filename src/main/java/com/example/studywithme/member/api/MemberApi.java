package com.example.studywithme.member.api;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.member.application.dto.MemberRequest;
import com.example.studywithme.member.application.dto.MemberResponse;
import com.example.studywithme.member.application.interact.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberApi {
    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity<Void> register(@Valid @RequestBody MemberRequest memberRequest) {
        memberService.register(memberRequest);
        return ResponseEntity.ok(null);
    }

    @GetMapping("")
    public ResponseEntity<MemberResponse> readInfo(@AuthenticationPrincipal UserDto userDto) {

        MemberResponse memberResponse = memberService.readInfo(userDto);
        return ResponseEntity.ok(memberResponse);
    }

    @PutMapping("/{mid}")
    public ResponseEntity<Void> modifyInfo(@Valid @RequestBody MemberRequest memberRequest,
                                           @PathVariable Long mid) {

        memberService.modifyInfo(memberRequest, mid);
        return ResponseEntity.ok(null);
    }
}
