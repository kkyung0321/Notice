package com.example.studywithme.member.application.interact;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.member.application.dto.MemberRequest;
import com.example.studywithme.member.application.dto.MemberResponse;
import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.post.application.dto.PostResponse;
import com.example.studywithme.reply.application.dto.ReplyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {
    Member findByUsername(String username);

    void register(MemberRequest memberRequest);

    MemberResponse readInfo(UserDto userDto);

    void modifyInfo(MemberRequest memberRequest, Long mid);

    Page<PostResponse> readMyPosts(UserDto userDto, Pageable pageable);

    Page<ReplyResponse> readMyReplies(UserDto userDto, Pageable pageable);
}
