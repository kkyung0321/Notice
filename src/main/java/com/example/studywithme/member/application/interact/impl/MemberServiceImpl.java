package com.example.studywithme.member.application.interact.impl;

import com.example.studywithme.global.error.exception.EntityNotFoundException;
import com.example.studywithme.global.error.exception.ErrorCode;
import com.example.studywithme.member.application.dao.MemberRepository;
import com.example.studywithme.member.application.dto.MemberRequest;
import com.example.studywithme.member.application.dto.MemberResponse;
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
        Member member = getMember(username);

        return member;
    }

    @Override
    public void register(MemberRequest memberRequest) {
        Member member = memberRequest.getMember();
        memberRepository.save(member);
    }

    @Override
    public MemberResponse readInfo(String username) {
        Member member = getMember(username);

        return MemberResponse.of(member);
    }

    private Member getMember(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> {
            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage());
        });
    }
}
