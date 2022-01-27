package com.example.studywithme.reply.application.dao;

import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.reply.application.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @Query("select r from Reply r where r.post.pid = :pid")
    List<Reply> findAllByPid(Long pid);

    List<Reply> findAllByMember(Member member);
}
