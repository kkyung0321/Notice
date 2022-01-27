package com.example.studywithme.reply.application.interact.impl;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.post.application.entity.Post;
import com.example.studywithme.post.application.interact.PostService;
import com.example.studywithme.reply.application.dao.ReplyRepository;
import com.example.studywithme.reply.application.dto.ReplyRequest;
import com.example.studywithme.reply.application.dto.ReplyResponse;
import com.example.studywithme.reply.application.entity.Reply;
import com.example.studywithme.reply.application.interact.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;

    private final PostService postService;

    @Override
    public void writeReply(UserDto userDto, Long pid, ReplyRequest replyRequest) {
        Reply reply = replyRequest.getReply();

        Post post = postService.getPost(pid);

        post.updateReply(reply);

        Member member = userDto.getMember();

        member.updateReply(reply);

        replyRepository.save(reply);
    }

    @Override
    public void modifyReply(Long rid, ReplyRequest replyRequest) {
        Reply reply = replyRepository.findById(rid).orElseThrow();
        reply.updateContent(replyRequest.getContent());
    }

    @Override
    public void deleteReply(Long rid) {
        replyRepository.deleteById(rid);
    }

    @Override
    public Page<ReplyResponse> readReplies(Long pid, Pageable pageable) {
        List<Reply> replies = replyRepository.findAllByPid(pid);
        return ReplyResponse.of(replies, pageable);
    }

    @Override
    public Page<ReplyResponse> readMyReplies(Member member, Pageable pageable) {
        List<Reply> replies = replyRepository.findAllByMember(member);
        return ReplyResponse.of(replies, pageable);
    }
}
