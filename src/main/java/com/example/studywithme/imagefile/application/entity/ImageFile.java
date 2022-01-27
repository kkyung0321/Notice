package com.example.studywithme.imagefile.application.entity;

import com.example.studywithme.post.application.entity.Post;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageFile {
    @Id
    @GeneratedValue
    private Long ifId;

    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @ToString.Exclude
    @JsonIgnore
    private Post post;

    @Builder
    public ImageFile(String path, Post post) {
        this.path = path;
        this.post = post;
    }

    public void updatePost(Post post) {
        this.post = post;
    }
}
