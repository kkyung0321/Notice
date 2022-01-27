package com.example.studywithme.member.api;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.member.application.dto.MemberRequest;
import com.example.studywithme.member.application.dto.MemberResponse;
import com.example.studywithme.member.application.interact.MemberService;
import com.example.studywithme.post.application.dto.PostResponse;
import com.example.studywithme.reply.application.dto.ReplyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponse>> readMyPosts(@AuthenticationPrincipal UserDto userDto,
                                                          @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC)
                                                                  Pageable pageable) {
        Page<PostResponse> postResponses = memberService.readMyPosts(userDto, pageable);
        return ResponseEntity.ok(postResponses);
    }

    @GetMapping("/replies")
    public ResponseEntity<Page<ReplyResponse>> readMyReplies(@AuthenticationPrincipal UserDto userDto,
                                                             @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC)
                                                                     Pageable pageable) {
        Page<ReplyResponse> replyResponses = memberService.readMyReplies(userDto, pageable);
        return ResponseEntity.ok(replyResponses);
    }
}
