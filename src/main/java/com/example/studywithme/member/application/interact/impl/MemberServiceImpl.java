package com.example.studywithme.member.application.interact.impl;

import com.example.studywithme.global.error.exception.EntityNotFoundException;
import com.example.studywithme.global.error.exception.ErrorCode;
import com.example.studywithme.member.application.dao.MemberRepository;
import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.member.application.interact.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    public Member findByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage());
                });

        return member;
    }
}
