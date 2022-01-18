package com.example.studywithme.post.application.dto;

import com.example.studywithme.imagefile.application.entity.ImageFile;
import com.example.studywithme.post.application.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
        this.nickname = post.getMember().getNickName();
        post.getImageFiles().forEach(imageFile -> this.imageFiles.add(imageFile));
    }

    public static PostResponse of(Post post) {
        return new PostResponse(post);
    }
}
