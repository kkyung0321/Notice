package com.example.studywithme.imagefile.application.entity;

import com.example.studywithme.post.application.entity.Post;
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
    private Post post;

    @Builder
    public ImageFile(String path, Post post) {
        this.path = path;
        this.post = post;
    }

    public void associateWithPost(Post post) {
        this.post = post;
        post.getImageFiles().add(this);
    }
}
