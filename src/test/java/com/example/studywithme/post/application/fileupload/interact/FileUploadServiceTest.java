package com.example.studywithme.post.application.fileupload.interact;

import com.example.studywithme.imagefile.application.dao.ImageFileRepository;
import com.example.studywithme.imagefile.application.entity.ImageFile;
import com.example.studywithme.imagefile.application.interact.impl.ImageFileServiceImpl;
import com.example.studywithme.post.application.dao.PostRepository;
import com.example.studywithme.post.application.dto.PostRequest;
import com.example.studywithme.post.application.entity.Post;
import com.example.studywithme.post.application.fileupload.interact.impl.FileUploadServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DataJpaTest
@ActiveProfiles("test")
@Import({FileUploadServiceImpl.class, ImageFileServiceImpl.class})
public class FileUploadServiceTest {

    @Autowired
    private ImageFileRepository imageFileRepository;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private PostRepository postRepository;

    @DisplayName("파일을 업로드하고 데이터를 저장한다")
    @Test
    void manageFile() throws Exception {
        //Arrange

        Post post = getPost();

        List<MultipartFile> multipartFiles = getMultipartFiles();

        //Act
        postRepository.save(post);
        fileUploadService.uploadFile(post, multipartFiles);

        //Assert
        List<ImageFile> imageFiles = imageFileRepository.findAll();
        assertThat(imageFiles).usingRecursiveFieldByFieldElementComparator()
                .extracting("post")
                .contains(post);
    }

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

    private Post getPost() {
        String title = "title";
        String content = "content";

        PostRequest postRequest = new PostRequest(title, content);

        Post post = postRequest.toEntity();
        return post;
    }
}
