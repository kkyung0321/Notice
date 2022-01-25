package com.example.studywithme.reply.application.entity;

import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.post.application.entity.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Reply {
    @Id @GeneratedValue
    private Long rid;

    private String content;

    @CreatedDate
    @JoinColumn(name = "created_date")
    private String createdDate;

    @LastModifiedDate
    @JoinColumn(name = "modified_date")
    private String modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Reply(String content) {
        this.content = content;
    }

    public void associateWithMember(Member member) {
        this.member = member;
        member.getReplies().add(this);
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updatePost(Post post) {
        this.post = post;
    }

    public void updateMember(Member member) {
        this.member = member;
    }
}
