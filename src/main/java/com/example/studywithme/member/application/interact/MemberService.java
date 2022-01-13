package com.example.studywithme.member.application.interact;

import com.example.studywithme.member.application.entity.Member;

public interface MemberService {
    Member findByUsername(String username);
}
