package com.example.studywithme.member.application.interact;

import com.example.studywithme.member.application.dto.MemberRequest;
import com.example.studywithme.member.application.dto.MemberResponse;
import com.example.studywithme.member.application.entity.Member;

public interface MemberService {
    Member findByUsername(String username);

    void register(MemberRequest memberRequest);

    MemberResponse readInfo(com.example.studywithme.global.auth.UserDto userDto);

    void modifyInfo(MemberRequest memberRequest, Long mid);
}
