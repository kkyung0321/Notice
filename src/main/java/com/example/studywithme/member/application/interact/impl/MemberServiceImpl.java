package com.example.studywithme.member.application.interact.impl;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.global.error.exception.ErrorCode;
import com.example.studywithme.global.error.exception.InvalidValueException;
import com.example.studywithme.member.application.dao.MemberRepository;
import com.example.studywithme.member.application.dto.MemberRequest;
import com.example.studywithme.member.application.dto.MemberResponse;
import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.member.application.interact.MemberService;
import com.example.studywithme.post.application.dto.PostResponse;
import com.example.studywithme.post.application.interact.PostService;
import com.example.studywithme.reply.application.dto.ReplyResponse;
import com.example.studywithme.reply.application.interact.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    private final PostService postService;

    private final ReplyService replyService;

    @Override
    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow();
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
    public MemberResponse readInfo(UserDto userDto) {
        Long mid = userDto.getMember().getMid();
        Member member = memberRepository.findById(mid).orElseThrow();

        return MemberResponse.of(member);
    }

    @Override
    public void modifyInfo(MemberRequest memberRequest, Long mid) {
        if (memberRepository.findByNickname(memberRequest.getNickname()).isPresent())
            throw new InvalidValueException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        else {
            Member member = memberRepository.findById(mid).orElseThrow();

            member.updateInfo(memberRequest);
        }
    }

    @Override
    public Page<PostResponse> readMyPosts(UserDto userDto, Pageable pageable) {
        Member member = userDto.getMember();
        return postService.readMyPosts(member, pageable);
    }

    @Override
    public Page<ReplyResponse> readMyReplies(UserDto userDto, Pageable pageable) {
        Member member = userDto.getMember();
        return replyService.readMyReplies(member, pageable);
    }
}
