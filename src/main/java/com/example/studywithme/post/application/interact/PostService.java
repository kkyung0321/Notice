package com.example.studywithme.post.application.interact;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.post.application.dto.PostRequest;
import com.example.studywithme.post.application.dto.PostResponse;
import com.example.studywithme.post.application.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    void writePost(com.example.studywithme.global.auth.UserDto userDto, PostRequest postRequest, List<MultipartFile> multipartFiles) throws Exception;

    PostResponse readPost(Long pid);

    void modifyPost(UserDto userDto, Long pid, PostRequest postRequest, List<MultipartFile> multipartFiles) throws Exception;

    void deletePost(Long pid);

    Page<PostResponse> readPosts(String search, Pageable pageable);

    Post getPost(Long pid);

    Page<PostResponse> readMyPosts(UserDto userDto, Pageable pageable);
}
