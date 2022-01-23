package com.example.studywithme.member.api;

import com.example.studywithme.member.application.dto.MemberRequest;
import com.example.studywithme.member.application.dto.MemberResponse;
import com.example.studywithme.member.application.interact.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{mid}")
    public ResponseEntity<MemberResponse> readInfo(@PathVariable Long mid) {

        MemberResponse memberResponse = memberService.readInfo(mid);
        return ResponseEntity.ok(memberResponse);
    }

    @PutMapping("/{mid}")
    public ResponseEntity<Void> modifyInfo(@Valid @RequestBody MemberRequest memberRequest,
                                           @PathVariable Long mid) {

        memberService.modifyInfo(memberRequest, mid);
        return ResponseEntity.ok(null);
    }
}
