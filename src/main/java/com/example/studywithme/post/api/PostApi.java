package com.example.studywithme.post.api;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.global.error.exception.ErrorCode;
import com.example.studywithme.global.error.exception.InvalidValueException;
import com.example.studywithme.post.application.dto.PostRequest;
import com.example.studywithme.post.application.dto.PostResponse;
import com.example.studywithme.post.application.interact.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @PutMapping("/{pid}")
    public ResponseEntity<Void> modifyPost(@AuthenticationPrincipal UserDto userDto,
                                           @Valid @RequestPart("postRequest") PostRequest postRequest,
                                           @RequestPart(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
                                           @PathVariable Long pid,
                                           @RequestParam("username") String username) throws Exception {

        if (userDto.getUsername().equals(username)) {
            postService.modifyPost(userDto, postRequest, multipartFiles, pid);
            return ResponseEntity.ok(null);
        } else {
            throw new InvalidValueException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }
    }

    @DeleteMapping("/{pid}")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal UserDto userDto,
                                           @PathVariable Long pid,
                                           @RequestParam("username") String username) {
        if (userDto.getUsername().equals(username)) {
            postService.deletePost(pid);
            return ResponseEntity.ok(null);
        } else {
            throw new InvalidValueException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<Page<PostResponse>> readPosts(@RequestParam(name = "search",
            required = false) String search, @PageableDefault(sort = "createdDate",
            direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostResponse> response = postService.readPosts(search, pageable);

        return ResponseEntity.ok(response);
    }
}
