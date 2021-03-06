package com.example.studywithme.post.application.entity;

import com.example.studywithme.imagefile.application.entity.ImageFile;
import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.post.application.dto.PostRequest;
import com.example.studywithme.reply.application.entity.Reply;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id
    @GeneratedValue
    private Long pid;

    private String title;

    private String content;

    private Long likeCounts;

    private Long hits;

    @CreatedDate
    private String createdDate;

    @LastModifiedDate
    private String modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "post")
    private List<ImageFile> imageFiles = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "post")
    private List<Reply> replies = new ArrayList<>();

    @Builder
    public Post(String title, String content, Long likeCounts, Long hits) {
        this.title = title;
        this.content = content;
        this.likeCounts = likeCounts;
        this.hits = hits;
    }

    public void increaseHits() {
        this.hits += 1l;
    }

    public void updatePost(PostRequest postRequest) {
        this.title = postRequest.getTitle();
        this.content = postRequest.getContent();
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateReply(Reply reply) {
        this.replies.add(reply);
        reply.updatePost(this);
    }

    public void updateImageFile(ImageFile imageFile) {
        this.imageFiles.add(imageFile);
        imageFile.updatePost(this);
    }
}
