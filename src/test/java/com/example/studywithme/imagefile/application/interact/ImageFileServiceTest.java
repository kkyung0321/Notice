package com.example.studywithme.imagefile.application.interact;

import com.example.studywithme.imagefile.application.dao.ImageFileRepository;
import com.example.studywithme.imagefile.application.interact.impl.ImageFileServiceImpl;
import com.example.studywithme.post.application.entity.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ImageFileServiceTest {

    @Mock
    private ImageFileRepository imageFileRepository;
    @InjectMocks
    private ImageFileServiceImpl imageFileService;

    private Post createPost() {
        String title = "title";
        String content = "content";
        Long hits = 0L;
        Long likeCounts = 0L;

        return Post.builder()
                .title(title)
                .content(content)
                .hits(hits)
                .likeCounts(likeCounts)
                .build();
    }

    @Test
    void saveImageFile() {
        //Arrange
        Post post = createPost();

        String path = "path";

        //Act
        imageFileService.saveImageFile(post, path);

        //Assert
        verify(imageFileRepository, times(1)).save(any());
    }
}
