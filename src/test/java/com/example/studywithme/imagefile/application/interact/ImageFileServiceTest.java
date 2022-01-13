package com.example.studywithme.imagefile.application.interact;

import com.example.studywithme.imagefile.application.dao.ImageFileRepository;
import com.example.studywithme.imagefile.application.entity.ImageFile;
import com.example.studywithme.imagefile.application.interact.impl.ImageFileServiceImpl;
import com.example.studywithme.post.application.dao.PostRepository;
import com.example.studywithme.post.application.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DataJpaTest
@ActiveProfiles("test")
@Import({ImageFileServiceImpl.class})
public class ImageFileServiceTest {

    @Autowired
    private ImageFileService imageFileService;
    @Autowired
    private ImageFileRepository imageFileRepository;
    @Autowired
    private PostRepository postRepository;

    @Test
    void should_save_image_file() {
        //Arrange
        Post post = createPost();

        List<String> paths = getPaths();

        //Act
        postRepository.save(post);

        for (String path : paths) {
            imageFileService.saveImageFile(post, path);
        }

        //Assert
        List<ImageFile> imageFiles = imageFileRepository.findAll();
        assertThat(imageFiles).hasAtLeastOneElementOfType(ImageFile.class);

        // image <-> post 연관관계 확인
        assertThat(imageFiles).usingRecursiveFieldByFieldElementComparator()
                .extracting("post")
                .contains(post);

        assertThat(post.getImageFiles()).usingRecursiveComparison()
                .isEqualTo(imageFiles);

    }

    private List<String> getPaths() {
        List<String> paths = new ArrayList<>();
        paths.add("path1");
        paths.add("path2");
        return paths;
    }

    private Post createPost() {
        String title = "title";
        String content = "content";
        Long hits = 0L;
        Long likeCounts = 0L;

        Post post = Post.builder()
                .title(title)
                .content(content)
                .hits(hits)
                .likeCounts(likeCounts)
                .build();
        return post;
    }
}
