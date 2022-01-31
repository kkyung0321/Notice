package com.example.studywithme.reply.application.interact;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.reply.application.dto.ReplyRequest;
import com.example.studywithme.reply.application.dto.ReplyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReplyService {
    void writeReply(com.example.studywithme.global.auth.UserDto userDto, Long pid, ReplyRequest replyRequest);

    void modifyReply(Long rid, ReplyRequest replyRequest);

    void deleteReply(Long rid);

    Page<ReplyResponse> readReplies(Long pid, Pageable pageable);

    Page<ReplyResponse> readMyReplies(UserDto userDto, Pageable pageable);
}
