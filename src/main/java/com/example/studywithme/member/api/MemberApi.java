package com.example.studywithme.member.api;

import com.example.studywithme.member.application.dto.MemberRequest;
import com.example.studywithme.member.application.dto.MemberResponse;
import com.example.studywithme.member.application.interact.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberApi {
    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody MemberRequest memberRequest) {
        memberService.register(memberRequest);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{username}")
    public ResponseEntity<MemberResponse> readInfo(@NotNull @PathVariable String username) {

        MemberResponse memberResponse = memberService.readInfo(username);
        return ResponseEntity.ok(memberResponse);
    }
}
