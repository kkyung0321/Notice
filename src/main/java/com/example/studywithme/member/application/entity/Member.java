package com.example.studywithme.member.application.entity;

import com.example.studywithme.post.application.entity.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
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

    @Builder
    public Member(String username, String password, String role, String nickname) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.nickname = nickname;
    }
}
