package com.example.studywithme.post.application.dao;

import com.example.studywithme.post.application.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p join fetch p.member join fetch p.imageFiles where p.pid = :pid")
    Post findPostByPidJoinMemberAndImage(@Param("pid") Long pid);
}
