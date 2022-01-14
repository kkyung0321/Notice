package com.example.studywithme.member.application.entity;

import com.example.studywithme.post.application.entity.Post;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    private String nickName;

    private String loginDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "member")
    private List<Post> posts = new ArrayList<>();
}
