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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private List<Post> createPosts() {
        List<Post> posts = new ArrayList<>();

        for (int i = 0; i < 33; i++) {
            Post post = Post.builder().title("title")
                    .content("content")
                    .likeCounts(0l)
                    .hits(0l)
                    .build();

            posts.add(post);
        }

        return posts;
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

    @DisplayName("이미지와 함께 글을 저장한다")
    @Test
    void writePostWithImageFiles() throws Exception {
        //Arrange
        PostRequest postRequest = createPostRequest();
        Member member = createMember();
        UserDto userDto = new UserDto(member);
        List<MultipartFile> multipartFiles = createMultipartFiles();

        //Act
        postService.writePost(userDto, postRequest, multipartFiles);

        //Assert
        assertThat(member.getPosts().size()).isEqualTo(1);
        verify(postRepository, times(1)).save(any());
        verify(fileUploadService, times(1)).uploadFile(any(), anyList());
    }

    @DisplayName("이미지와 함께 게시글을 조회한다")
    @Test
    void readPost() {
        //Arrange
        Post post = createPost();
        Member member = createMember();
        member.updatePost(post);
        List<ImageFile> imageFiles = createImageFile();
        for (ImageFile imageFile : imageFiles) {
            post.updateImageFile(imageFile);
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

    @DisplayName("글만 수정한다")
    @Test
    void modifyPost() throws Exception {
        //Arrange
        Member member = createMember();
        UserDto userDto = new UserDto(member);

        Post post = createPost();
        Long pid = post.getPid();

        PostRequest postRequest = createPostRequest();

        given(postRepository.findById(pid)).willReturn(Optional.ofNullable(post));

        //Act
        postService.modifyPost(userDto, pid, postRequest, null);

        //Assert
        verify(imageFileService, times(1)).deleteImageFilesByPid(any());
        verify(postRepository, times(1)).findById(any());
        verify(fileUploadService, times(0)).uploadFile(any(), any());
        assertThat(post.getTitle()).isEqualTo(postRequest.getTitle());
        assertThat(post.getContent()).isEqualTo(postRequest.getContent());
    }

    @DisplayName("이미지와 함께 글을 수정한다")
    @Test
    void modifyPostWithImage() throws Exception {
        //Arrange
        Member member = createMember();
        UserDto userDto = new UserDto(member);

        Post post = createPost();
        Long pid = post.getPid();

        PostRequest postRequest = createPostRequest();

        List<MultipartFile> multipartFiles = createMultipartFiles();

        given(postRepository.findById(pid)).willReturn(Optional.ofNullable(post));

        //Act
        postService.modifyPost(userDto, pid, postRequest, multipartFiles);

        //Assert
        verify(imageFileService, times(1)).deleteImageFilesByPid(any());
        verify(postRepository, times(1)).findById(any());
        verify(fileUploadService, times(1)).uploadFile(any(), any());
        assertThat(post.getTitle()).isEqualTo(postRequest.getTitle());
        assertThat(post.getContent()).isEqualTo(postRequest.getContent());
    }

    @DisplayName("검색없이 모든 게시물을 조회한다")
    @Test
    void readPosts() {
        //Arrange
        String search = null;
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdDate");

        //Act
        postService.readPosts(search, pageable);

        //Assert
        verify(postRepository, times(1)).findAll();
    }

    @DisplayName("검색조건과 함께 게시물을 조회한다")
    @Test
    void readPostsWithSearch() {
        //Arrange
        List<Post> posts = createPosts();
        String search = "search";
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdDate");

        Member member = createMember();
        UserDto userDto = new UserDto(member);

        for (Post post : posts) {
            member.updatePost(post);
        }

        given(postRepository.findAllBySearch(search)).willReturn(posts);

        //Act
        Page<PostResponse> postResponses = postService.readPosts(search, pageable);

        //Assert
        verify(postRepository, times(1)).findAllBySearch(search);
        assertThat(postResponses.getNumber()).isEqualTo(0);
        assertThat(postResponses.getSize()).isEqualTo(10);
        assertThat(postResponses.getTotalElements()).isEqualTo(33);
        assertThat(postResponses.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdDate"));
        assertThat(postResponses.getContent()).usingRecursiveFieldByFieldElementComparator()
                .extracting("title")
                .allMatch(o -> String.valueOf(o).matches("^(title).*$"));
    }

    @Test
    void readMyPosts() {
        //Arrange
        Member member = createMember();
        UserDto userDto = new UserDto(member);

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdDate");

        //Act
        postService.readMyPosts(userDto, pageable);

        //Assert
        verify(postRepository, times(1)).findAllByMember(member);
    }
}
