package com.example.studywithme.post.application.dto;

import com.example.studywithme.imagefile.application.entity.ImageFile;
import com.example.studywithme.post.application.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class PostResponse {
    private Long pid;

    private String title;

    private String content;

    private Long likeCounts;

    private Long hits;

    private String createdDate;

    private String modifiedDate;

    private String username;

    private String nickname;

    private List<ImageFile> imageFiles = new ArrayList<>();

    public PostResponse(Post post) {
        this.pid = post.getPid();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.likeCounts = post.getLikeCounts();
        this.hits = post.getHits();
        this.createdDate = post.getCreatedDate();
        this.modifiedDate = post.getModifiedDate();
        this.username = post.getMember().getUsername();
        this.nickname = post.getMember().getNickname();
        post.getImageFiles().forEach(imageFile -> this.imageFiles.add(imageFile));
    }

    public static PostResponse of(Post post) {
        return new PostResponse(post);
    }

    @Builder
    public PostResponse(Long pid, String title, String content, Long likeCounts, Long hits, String createdDate, String modifiedDate, String username, String nickname) {
        this.pid = pid;
        this.title = title;
        this.content = content;
        this.likeCounts = likeCounts;
        this.hits = hits;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.username = username;
        this.nickname = nickname;
    }

    public static Page<PostResponse> of(List<Post> posts, Pageable pageable) {
        List<PostResponse> postResponses = posts.stream().map(post -> PostResponse.builder()
                .pid(post.getPid())
                .title(post.getTitle())
                .content(post.getContent())
                .likeCounts(post.getLikeCounts())
                .hits(post.getHits())
                .createdDate(post.getCreatedDate())
                .modifiedDate(post.getModifiedDate())
                .username(post.getMember().getUsername())
                .nickname(post.getMember().getNickname())
                .build()).collect(Collectors.toList());

        return new PageImpl<>(postResponses, pageable, postResponses.size());
    }
}
