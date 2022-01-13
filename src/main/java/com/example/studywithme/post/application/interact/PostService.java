package com.example.studywithme.post.application.interact;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.post.application.dto.PostRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    void writePost(UserDto userDto, PostRequest postRequest, List<MultipartFile> multipartFiles) throws Exception;
}
