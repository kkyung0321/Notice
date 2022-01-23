package com.example.studywithme.member.application.dto;

import com.example.studywithme.member.application.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRequest {

    @NotBlank(message = "아이디를 입력해주세요")
    @Size(max = 30, message = "아이디는 30글자 이하로 입력해주세요")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(max = 30, message = "비밀번호는 30글자 이하로 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*])[A-Za-z\\d~!@#$%^&*]{8,}$")
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요")
    @Size(max = 30, message = "닉네임은 30글자 이하로 입력해주세요")
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
