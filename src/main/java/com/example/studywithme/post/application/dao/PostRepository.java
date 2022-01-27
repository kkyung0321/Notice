package com.example.studywithme.post.application.dao;

import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.post.application.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p join fetch p.member join fetch p.imageFiles where p.pid = :pid")
    Optional<Post> findPostByPid(@Param("pid") Long pid);

    @Query("select p from Post p where p.title like concat('%', :search, '%') or p.member.nickname like concat('%', :search, '%')")
    List<Post> findAllBySearch(@Param("search") String search);

    List<Post> findAllByMember(Member member);
}
