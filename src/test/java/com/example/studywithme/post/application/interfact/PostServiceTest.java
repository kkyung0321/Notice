package com.example.studywithme.post.application.interfact;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.imagefile.application.entity.ImageFile;
import com.example.studywithme.imagefile.application.interact.ImageFileService;
import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.post.application.dao.PostRepository;
import com.example.studywithme.post.application.dto.PostRequest;
import com.example.studywithme.post.application.dto.PostResponse;
import com.example.studywithme.post.application.entity.Post;
import com.example.studywithme.post.application.fileupload.interact.FileUploadService;
import com.example.studywithme.post.application.interact.impl.PostServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private FileUploadService fileUploadService;
    @Mock
    private ImageFileService imageFileService;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private PostServiceImpl postService;

    private PostRequest createPostRequest() {
        return new PostRequest("title", "content");
    }

    private List<MultipartFile> createMultipartFiles() {
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("hello1.txt", "",
                MediaType.MULTIPART_FORM_DATA_VALUE, "hello world!".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("hello2.txt", "",
                MediaType.MULTIPART_FORM_DATA_VALUE, "hello world2!".getBytes(StandardCharsets.UTF_8));

        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(mockMultipartFile1);
        multipartFiles.add(mockMultipartFile2);

        return multipartFiles;
    }

    private Post createPost() {
        Post post = Post.builder()
                .title("title")
                .content("content")
                .hits(0L)
                .likeCounts(0L)
                .build();

        return post;
    }

    private List<ImageFile> createImageFile() {
        ImageFile imageFile = ImageFile.builder()
                .path("path")
                .build();

        ImageFile imageFile1 = ImageFile.builder().path("path2").build();

        List<ImageFile> imageFiles = new ArrayList<>();
        imageFiles.add(imageFile);
        imageFiles.add(imageFile1);

        return imageFiles;
    }

    private Member createMember() {
        Member member = Member.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .role("ROLE_USER")
                .build();

        return member;
    }

    @DisplayName("이미지 없이 글을 저장한다")
    @Test
    void writePost() throws Exception {
        //Arrange
        PostRequest postRequest = createPostRequest();
        Member member = createMember();
        UserDto userDto = new UserDto(member);

        //Act
        postService.writePost(userDto, postRequest, null);

        //Assert
        assertThat(member.getPosts().size()).isEqualTo(1);
        verify(postRepository, times(1)).save(any());
        verify(fileUploadService, times(0)).uploadFile(any(), anyList());
    }

    @DisplayName("이미지와 함께 게시글을 조회한다")
    @Test
    void readPost() {
        //Arrange
        Post post = createPost();
        Member member = createMember();
        post.associateWithMember(member);
        List<ImageFile> imageFiles = createImageFile();
        for (ImageFile imageFile : imageFiles) {
            imageFile.associateWithPost(post);
        }
        Long pid = post.getPid();

        given(postRepository.findPostByPid(pid)).willReturn(Optional.ofNullable(post));

        //Act
        PostResponse postResponse = postService.readPost(pid);

        // Assert
        assertThat(postResponse.getPid()).isEqualTo(post.getPid());
        assertThat(postResponse.getTitle()).isEqualTo(post.getTitle());
        assertThat(postResponse.getContent()).isEqualTo(post.getContent());
        assertThat(postResponse.getLikeCounts()).isEqualTo(post.getLikeCounts());
        assertThat(postResponse.getHits()).isEqualTo(post.getHits());
        assertThat(postResponse.getUsername()).isEqualTo(post.getMember().getUsername());
        assertThat(postResponse.getNickname()).isEqualTo(post.getMember().getNickname());
        assertThat(postResponse.getImageFiles()).usingRecursiveFieldByFieldElementComparator()
                .containsAll(post.getImageFiles());
    }
}
