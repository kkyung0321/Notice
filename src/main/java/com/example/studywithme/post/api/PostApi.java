package com.example.studywithme.post.api;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.post.application.dto.PostRequest;
import com.example.studywithme.post.application.dto.PostResponse;
import com.example.studywithme.post.application.interact.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostApi {

    private final PostService postService;

    @PostMapping("")
    public ResponseEntity<Void> writePost(@AuthenticationPrincipal UserDto userDto,
                                          @Valid @RequestPart("postRequest") PostRequest postRequest,
                                          @RequestPart(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles) throws Exception {

        postService.writePost(userDto, postRequest, multipartFiles);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{pid}")
    public ResponseEntity<PostResponse> readPost(@PathVariable Long pid) {
        PostResponse postResponse = postService.readPost(pid);
        return ResponseEntity.ok(postResponse);
    }
    
}
