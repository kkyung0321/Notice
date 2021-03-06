package com.example.studywithme.member.application.entity;

import com.example.studywithme.member.application.dto.MemberRequest;
import com.example.studywithme.post.application.entity.Post;
import com.example.studywithme.reply.application.entity.Reply;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue
    private Long mid;

    private String username;

    private String password;

    @Column(name = "member_role")
    private String role;

    private String nickname;

    private String loginDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "member")
    private List<Reply> replies = new ArrayList<>();

    @Builder
    public Member(String username, String password, String role, String nickname) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.nickname = nickname;
    }

    public void updateInfo(MemberRequest memberRequest) {
        this.nickname = memberRequest.getNickname();
    }

    public void updatePost(Post post) {
        this.getPosts().add(post);
        post.updateMember(this);
    }

    public void updateReply(Reply reply) {
        this.replies.add(reply);
        reply.updateMember(this);
    }

    public void updateLoginDate(Date date) {
        this.loginDate = date.toString();
    }
}
