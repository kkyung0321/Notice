package com.example.studywithme.reply.api;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.global.error.exception.ErrorCode;
import com.example.studywithme.global.error.exception.InvalidValueException;
import com.example.studywithme.reply.application.dto.ReplyRequest;
import com.example.studywithme.reply.application.dto.ReplyResponse;
import com.example.studywithme.reply.application.interact.ReplyService;
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
@RequestMapping("/replies")
public class ReplyApi {
    private final ReplyService replyService;

    @PostMapping("")
    public ResponseEntity<Void> writeReply(@AuthenticationPrincipal UserDto userDto,
                                           @RequestParam("pid") Long pid,
                                           @Valid @RequestBody ReplyRequest replyRequest) {
        replyService.writeReply(userDto, pid, replyRequest);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/{rid}")
    public ResponseEntity<Void> modifyReply(@AuthenticationPrincipal UserDto userDto,
                                            @PathVariable("rid") Long rid,
                                            @Valid @RequestBody ReplyRequest replyRequest,
                                            @RequestParam("username") String username) {

        if (userDto.getMember().getUsername().equals(username)) {
            replyService.modifyReply(rid, replyRequest);
            return ResponseEntity.ok(null);
        } else {
            throw new InvalidValueException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }
    }

    @DeleteMapping("/{rid}")
    public ResponseEntity<Void> deleteReply(@AuthenticationPrincipal UserDto userDto,
                                            @PathVariable("rid") Long rid,
                                            @RequestParam("username") String username) {
        if (userDto.getMember().getUsername().equals(username)) {
            replyService.deleteReply(rid);
            return ResponseEntity.ok(null);
        } else {
            throw new InvalidValueException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<Page<ReplyResponse>> readReplies(@RequestParam("pid") Long pid,
                                                           @PageableDefault(sort = "createdDate",
                                                                   direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReplyResponse> replies = replyService.readReplies(pid, pageable);
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/members")
    public ResponseEntity<Page<ReplyResponse>> readMyReplies(@AuthenticationPrincipal UserDto userDto,
                                                             @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC)
                                                                     Pageable pageable) {
        Page<ReplyResponse> replyResponses = replyService.readMyReplies(userDto, pageable);
        return ResponseEntity.ok(replyResponses);
    }
}
