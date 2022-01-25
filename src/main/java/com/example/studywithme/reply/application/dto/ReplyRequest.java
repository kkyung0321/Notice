package com.example.studywithme.reply.application.dto;

import com.example.studywithme.reply.application.entity.Reply;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyRequest {

    @NotBlank(message = "댓글을 입력해주세요")
    private String content;

    public Reply toEntity() {
        return Reply.builder()
                .content(content)
                .build();
    }

    public ReplyRequest(String content) {
        this.content = content;
    }

    public Reply getReply() {
        return toEntity();
    }
}
