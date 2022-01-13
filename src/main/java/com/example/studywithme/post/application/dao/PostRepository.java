package com.example.studywithme.post.application.dao;

import com.example.studywithme.post.application.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
