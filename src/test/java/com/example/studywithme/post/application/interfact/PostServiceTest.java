package com.example.studywithme.post.application.interfact;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.global.error.exception.EntityNotFoundException;
import com.example.studywithme.global.error.exception.ErrorCode;
import com.example.studywithme.imagefile.application.dao.ImageFileRepository;
import com.example.studywithme.imagefile.application.entity.ImageFile;
import com.example.studywithme.imagefile.application.interact.impl.ImageFileServiceImpl;
import com.example.studywithme.member.application.dao.MemberRepository;
import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.post.application.dao.PostRepository;
import com.example.studywithme.post.application.dto.PostRequest;
import com.example.studywithme.post.application.dto.PostResponse;
import com.example.studywithme.post.application.entity.Post;
import com.example.studywithme.post.application.fileupload.interact.impl.FileUploadServiceImpl;
import com.example.studywithme.post.application.interact.PostService;
import com.example.studywithme.post.application.interact.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@DataJpaTest
@Import({PostServiceImpl.class, FileUploadServiceImpl.class, ImageFileServiceImpl.class})
public class PostServiceTest {

    @Autowired
    private ImageFileRepository imageFileRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;

    @Test
    @Sql(scripts = "classpath:db/test/member.sql")
    void should_create_post_if_member_is_logged_in() throws Exception {
        //Arrange

        Member member = memberRepository.findAll().get(0);
        UserDto userDto = new UserDto(member);

        PostRequest postRequest = getPostRequest();

        List<MultipartFile> multipartFiles = getMultipartFiles();

        //Act
        postService.writePost(userDto, postRequest, multipartFiles);

        //Assert

        List<Post> posts = Optional.ofNullable(postRepository.findAll())
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage());
                });
        Post post = posts.get(0);

        List<ImageFile> imageFiles = Optional.ofNullable(imageFileRepository.findAll())
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage());
                });

        assertThat(imageFiles).hasAtLeastOneElementOfType(ImageFile.class);
        assertThat(imageFiles).usingRecursiveFieldByFieldElementComparator()
                .extracting("post")
                .contains(post);
    }

    private PostRequest getPostRequest() {
        String title = "title";
        String content = "content";

        PostRequest postRequest = new PostRequest(title, content);
        return postRequest;
    }

    private List<MultipartFile> getMultipartFiles() {
        MockMultipartFile multipartFile1 = new MockMultipartFile(
                "file1", "hello1.jpg", null, "hello world1".getBytes()
        );

        MockMultipartFile multipartFile2 = new MockMultipartFile(
                "file2", "hello2.jpg", null, "hello world2".getBytes()
        );

        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(multipartFile1);
        multipartFiles.add(multipartFile2);
        return multipartFiles;
    }

    @Test
    @Sql(scripts = "classpath:db/test/post_associated_images.sql")
    void should_read_post_and_associated_image_files() {
        //Arrange
        Long pid = 1l;

        //Act
        PostResponse postResponse = postService.readPost(pid);

        // Assert
        assertThat(postResponse.getPid()).isEqualTo(pid);
        assertThat(postResponse.getNickname()).isEqualTo("nickname");
        assertThat(postResponse.getImageFiles()).usingRecursiveFieldByFieldElementComparator()
                .extracting("path")
                .containsAll(Arrays.asList("path", "path2"));
        assertThat(postResponse.getHits()).isEqualTo(1l);
    }
}
