package com.example.studywithme.member.application.interact.impl;

import com.example.studywithme.global.error.exception.EntityNotFoundException;
import com.example.studywithme.global.error.exception.ErrorCode;
import com.example.studywithme.global.error.exception.InvalidValueException;
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
        return getMember(username);
    }

    @Override
    public void register(MemberRequest memberRequest) {
        if (memberRepository.findByUsername(memberRequest.getUsername()).isPresent() ||
                memberRepository.findByNickname(memberRequest.getNickname()).isPresent())
            throw new InvalidValueException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        else {
            Member member = memberRequest.getMember();
            memberRepository.save(member);
        }
    }

    @Override
    public MemberResponse readInfo(Long mid) {
        Member member = getMember(mid);

        return MemberResponse.of(member);
    }

    @Override
    public void modifyInfo(MemberRequest memberRequest, Long mid) {
        if (memberRepository.findByNickname(memberRequest.getNickname()).isPresent())
            throw new InvalidValueException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        else {
            Member member = getMember(mid);

            member.updateInfo(memberRequest);
        }
    }

    private Member getMember(Long mid) {
        return memberRepository.findById(mid).orElseThrow(() -> {
            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage());
        });
    }

    private Member getMember(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> {
            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage());
        });
    }
}
