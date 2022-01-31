package com.example.studywithme.post.application.fileupload.interact;

import com.example.studywithme.imagefile.application.interact.ImageFileService;
import com.example.studywithme.post.application.entity.Post;
import com.example.studywithme.post.application.fileupload.interact.impl.FileUploadServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class FileUploadServiceTest {

    @Mock
    private ImageFileService imageFileService;
    @InjectMocks
    private FileUploadServiceImpl fileUploadService;

    private List<MultipartFile> getMultipartFiles() {
        MockMultipartFile multipartFile1 = new MockMultipartFile(
                "file1", "hello1.txt", null, "hello world1".getBytes()
        );

        MockMultipartFile multipartFile2 = new MockMultipartFile(
                "file2", "hello2.txt", null, "hello world2".getBytes()
        );

        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(multipartFile1);
        multipartFiles.add(multipartFile2);
        return multipartFiles;
    }

    private Post createPost() {
        return Post.builder()
                .title("title")
                .content("content")
                .hits(0L)
                .likeCounts(0L)
                .build();
    }

    @DisplayName("파일을 업로드하고 데이터를 저장한다")
    @Test
    void uploadFile() throws Exception {
        //Arrange

        Post post = createPost();

        List<MultipartFile> multipartFiles = getMultipartFiles();

        //Act
        fileUploadService.uploadFile(post, multipartFiles);

        //Assert
        verify(imageFileService, atLeast(1)).saveImageFile(any(), any());
    }
}
