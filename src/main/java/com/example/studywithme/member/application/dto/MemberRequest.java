package com.example.studywithme.member.application.dto;

import com.example.studywithme.member.application.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRequest {
    private String username;

    private String password;

    private String nickname;

    private Member toEntity() {
        return Member.builder()
                .username(username)
                .password(encrypt(password))
                .nickname(nickname)
                .role("ROLE_USER")
                .build();
    }

    private String encrypt(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public Member getMember() {
        return toEntity();
    }

    public MemberRequest(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
}
