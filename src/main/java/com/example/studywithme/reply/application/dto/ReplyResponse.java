package com.example.studywithme.reply.application.dto;

import com.example.studywithme.reply.application.entity.Reply;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyResponse {
    private Long rid;

    private String content;

    private String createdDate;

    private String modifiedDate;

    private Long mid;

    private String username;

    private String nickname;

    private Long pid;

    private String title;

    @Builder
    public ReplyResponse(Long rid, String content, String createdDate, String modifiedDate, Long mid, String username, String nickname, Long pid, String title) {
        this.rid = rid;
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.mid = mid;
        this.username = username;
        this.nickname = nickname;
        this.pid = pid;
        this.title = title;
    }

    public static Page<ReplyResponse> of(List<Reply> replies, Pageable pageable) {
        List<ReplyResponse> replyResponses = replies.stream().map(reply -> ReplyResponse.builder()
                .rid(reply.getRid())
                .content(reply.getContent())
                .createdDate(reply.getCreatedDate())
                .modifiedDate(reply.getModifiedDate())
                .mid(reply.getMember().getMid())
                .username(reply.getMember().getUsername())
                .nickname(reply.getMember().getNickname())
                .pid(reply.getPost().getPid())
                .title(reply.getPost().getTitle()).build())
                .collect(Collectors.toList());

        return new PageImpl<>(replyResponses, pageable, replyResponses.size());
    }
}
