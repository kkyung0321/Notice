package com.example.studywithme.post.application.interact.impl;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.global.error.exception.EntityNotFoundException;
import com.example.studywithme.global.error.exception.ErrorCode;
import com.example.studywithme.post.application.dao.PostRepository;
import com.example.studywithme.post.application.dto.PostRequest;
import com.example.studywithme.post.application.dto.PostResponse;
import com.example.studywithme.post.application.entity.Post;
import com.example.studywithme.post.application.fileupload.interact.FileUploadService;
import com.example.studywithme.post.application.interact.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    private final FileUploadService fileUploadService;

    @Override
    public void writePost(UserDto userDto, PostRequest postRequest, List<MultipartFile> multipartFiles) throws Exception {
        Post post = postRequest.getPost();
        post.associateWithMember(userDto.getMember());
        postRepository.save(post);
        fileUploadService.manageFile(post, multipartFiles);
    }

    @Override
    public PostResponse readPost(Long pid) {
        Post post = getPost(pid);

        post.increaseHits();

        return PostResponse.of(post);
    }

    private Post getPost(Long pid) {
        Post post = Optional.ofNullable(postRepository.findPostByPidJoinMemberAndImage(pid)).orElseThrow(
                () -> {
                    throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage());
                });
        return post;
    }
}
