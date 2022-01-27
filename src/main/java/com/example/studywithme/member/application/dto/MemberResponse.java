package com.example.studywithme.member.application.dto;

import com.example.studywithme.member.application.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponse {
    private Long mid;

    private String username;

    private String password;

    private String nickname;

    private String loginDate;

    private String role;

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .mid(member.getMid())
                .username(member.getUsername())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .loginDate(member.getLoginDate())
                .role(member.getRole())
                .build();
    }

    @Builder
    public MemberResponse(Long mid, String username, String password, String nickname, String loginDate, String role) {
        this.mid = mid;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.loginDate = loginDate;
        this.role = role;
    }
}
