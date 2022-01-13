package com.example.studywithme.post.application.dto;

import com.example.studywithme.post.application.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class PostRequest {
    @NotBlank(message = "제목을 입력해주세요")
    private String title;

    @NotBlank(message = "본문을 입력해주세요")
    private String content;

    public PostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .likeCounts(0L)
                .hits(0L)
                .build();
    }

    public Post getPost() {
        return toEntity();
    }
}
