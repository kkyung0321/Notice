package com.example.studywithme.post.application.interact.impl;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.imagefile.application.interact.ImageFileService;
import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.post.application.dao.PostRepository;
import com.example.studywithme.post.application.dto.PostRequest;
import com.example.studywithme.post.application.dto.PostResponse;
import com.example.studywithme.post.application.entity.Post;
import com.example.studywithme.post.application.fileupload.interact.FileUploadService;
import com.example.studywithme.post.application.interact.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    private final FileUploadService fileUploadService;

    private final ImageFileService imageFileService;

    @Override
    public void writePost(UserDto userDto, PostRequest postRequest,
                          List<MultipartFile> multipartFiles) throws Exception {
        Post post = postRequest.getPost();

        Member member = userDto.getMember();

        member.updatePost(post);

        if (multipartFiles != null) {
            fileUploadService.uploadFile(post, multipartFiles);
        }

        postRepository.save(post);
    }

    @Override
    public PostResponse readPost(Long pid) {
        Post post = postRepository.findPostByPid(pid).orElseThrow();

        post.increaseHits();

        return PostResponse.of(post);
    }

    @Override
    public void modifyPost(UserDto userDto, Long pid, PostRequest postRequest, List<MultipartFile> multipartFiles) throws Exception {
        imageFileService.deleteImageFilesByPid(pid);

        Post post = postRepository.findById(pid).orElseThrow();

        post.updatePost(postRequest);

        if (multipartFiles != null)
            fileUploadService.uploadFile(post, multipartFiles);
    }

    @Override
    public void deletePost(Long pid) {
        postRepository.deleteById(pid);
    }

    @Override
    public Page<PostResponse> readPosts(String search, Pageable pageable) {
        if (search == null)
            return PostResponse.of(postRepository.findAll(), pageable);
        else {
            List<Post> searchList = getSearchList(search);

            if (searchList != null && searchList.size() > 0)
                return PostResponse.of(searchList, pageable);
            else
                return new PageImpl<>(new ArrayList<>());
        }
    }

    private List<Post> getSearchList(String search) {
        return postRepository.findAllBySearch(search);
    }

    @Override
    public Post getPost(Long pid) {
        return postRepository.findById(pid).orElseThrow();
    }

    @Override
    public Page<PostResponse> readMyPosts(UserDto userDto, Pageable pageable) {
        Member member = userDto.getMember();
        List<Post> posts = postRepository.findAllByMember(member);
        return PostResponse.of(posts, pageable);
    }
}
