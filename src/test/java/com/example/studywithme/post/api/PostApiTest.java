package com.example.studywithme.post.api;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.global.error.exception.EntityNotFoundException;
import com.example.studywithme.global.error.exception.ErrorCode;
import com.example.studywithme.global.error.exception.InvalidValueException;
import com.example.studywithme.imagefile.application.dao.ImageFileRepository;
import com.example.studywithme.imagefile.application.entity.ImageFile;
import com.example.studywithme.member.application.dao.MemberRepository;
import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.post.application.dao.PostRepository;
import com.example.studywithme.post.application.dto.PostRequest;
import com.example.studywithme.post.application.dto.PostResponse;
import com.example.studywithme.post.application.entity.Post;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Transactional
@SpringBootTest
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
public class PostApiTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ImageFileRepository imageFileRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private Member getMember() {
        Member member = Optional.ofNullable(memberRepository.findById(1l))
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage());
                }).get();
        return member;
    }

    private MockMultipartFile getMultipartFile(String originalName, String contentName) {
        return new MockMultipartFile(
                "multipartFiles", originalName,
                MediaType.MULTIPART_FORM_DATA_VALUE, contentName.getBytes()
        );
    }

    private MockMultipartFile getPostRequestMultipart(String title, String content) throws JsonProcessingException {
        PostRequest postRequest = new PostRequest(title, content);

        MockMultipartFile postRequestFile = new MockMultipartFile("postRequest",
                "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(postRequest).getBytes(StandardCharsets.UTF_8));

        return postRequestFile;
    }

    @DisplayName("로그인이 되어 있으면 글을 쓸 수 있다")
    @Test
    @Sql(scripts = "classpath:db/test/member.sql")
    void writePost() throws Exception {

        //Arrange
        String title = "title";
        String content = "content";

        MockMultipartFile postRequestMultipart = getPostRequestMultipart(title, content);

        MockMultipartFile multipartFile1 = getMultipartFile("hello1.txt", "hello world1");

        MockMultipartFile multipartFile2 = getMultipartFile("hello2.txt", "hello world2");

        UserDto userDto = new UserDto(getMember());

        //Act
        mockMvc.perform(multipart("/posts")
                .file(postRequestMultipart)
                .file(multipartFile1)
                .file(multipartFile2)
                .with(user(userDto))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(authenticated().withUsername("username1"))
                .andDo(print());

        //Assert
        // post 입력값 검증
        List<Post> posts = Optional.ofNullable(postRepository.findAll())
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage());
                });

        assertThat(posts).usingRecursiveFieldByFieldElementComparator()
                .extracting(title, content, "likeCounts", "hits")
                .contains(tuple(title, content, 0L, 0L));

        // member - post 연관관계 검증
        Post post = posts.get(0);
        assertThat(post.getMember()).isEqualTo(getMember());
        assertThat(getMember().getPosts().get(0)).isEqualTo(post);

        // 업로드 파일 검증
        List<ImageFile> imageFiles = Optional.ofNullable(imageFileRepository.findAll())
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage());
                });

        assertThat(imageFiles).hasAtLeastOneElementOfType(ImageFile.class);
        assertThat(imageFiles).usingRecursiveFieldByFieldElementComparator()
                .extracting("post")
                .contains(post);
    }

    @Test
    @Sql(scripts = "classpath:db/test/postAndImages.sql")
    void readPost() throws Exception {
        //Arrange
        Long pid = 1l;

        //Act
        MockHttpServletResponse response = mockMvc.perform(get("/posts/{pid}", pid))
                .andDo(print())
                .andReturn().getResponse();

        //Assert
        // post, fetch join member, fetch join images , hits 증가 확인
        PostResponse postResponse = objectMapper.readValue(response.getContentAsString(), PostResponse.class);
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(postResponse));

        assertThat(postResponse.getPid()).isEqualTo(pid);

        assertThat(postResponse.getNickname()).isEqualTo("nickname");

        assertThat(postResponse.getImageFiles()).usingRecursiveFieldByFieldElementComparator()
                .extracting("path")
                .containsAll(Arrays.asList("path", "path2"));

        assertThat(postResponse.getHits()).isEqualTo(1l);
    }

    @DisplayName("로그인 유저와 글쓴이가 같지 않으면 예외를 던진다")
    @Test
    @Sql(scripts = {"classpath:db/test/postAndImages.sql"})
    void throwExceptionWhenModifyPost() throws Exception {
        //Arrange
        String modified_title = "modified title";
        String modified_content = "modified content";

        MockMultipartFile postRequestMultipart = getPostRequestMultipart(modified_title, modified_content);

        String originalName = "modified_hello1.txt";
        String contentName = "modified hello world1";
        MockMultipartFile multipartFile = getMultipartFile(originalName, contentName);

        String originalName2 = "modified_hello2.txt";
        String contentName2 = "modified hello world2";
        MockMultipartFile multipartFile1 = getMultipartFile(originalName2, contentName2);

        Long pid = 1l;

        UserDto userDto = new UserDto(getMember());

        String username = "username1";

        //Act
        mockMvc.perform(multipart("/posts/{pid}", pid)
                .file(postRequestMultipart)
                .file(multipartFile)
                .file(multipartFile1)
                .with(request -> {
                    request.setMethod(HttpMethod.PUT.name());
                    return request;
                })
                .with(user(userDto))
                .param("username", username)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .isEqualTo(ErrorCode.INVALID_INPUT_VALUE.getMessage()));
    }

    @DisplayName("로그인 유저와 글쓴이가 같으면 수정을 한다")
    @Test
    @Sql(scripts = {"classpath:db/test/postAndImages.sql"})
    void modifyPost() throws Exception {
        //Arrange
        String modified_title = "modified title";
        String modified_content = "modified content";

        MockMultipartFile postRequestMultipart = getPostRequestMultipart(modified_title, modified_content);

        String originalName = "modified_hello1.txt";
        String contentName = "modified hello world1";
        MockMultipartFile multipartFile = getMultipartFile(originalName, contentName);

        String originalName2 = "modified_hello2.txt";
        String contentName2 = "modified hello world2";
        MockMultipartFile multipartFile1 = getMultipartFile(originalName2, contentName2);

        Long pid = 1l;

        UserDto userDto = new UserDto(getMember());

        String username = "username";
        //Act
        mockMvc.perform(multipart("/posts/{pid}", pid)
                .file(postRequestMultipart)
                .file(multipartFile)
                .file(multipartFile1)
                .param("username", username)
                .with(request -> {
                    request.setMethod(HttpMethod.PUT.name());
                    return request;
                })
                .with(user(userDto))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(authenticated());

        //Assert
        List<Post> posts = postRepository.findAll();

        // post 가 변경되었는지 확인
        assertThat(posts).usingRecursiveFieldByFieldElementComparator()
                .extracting("title", "content")
                .contains(tuple(modified_title, modified_content));

        // member - post 연관관계 검증
        Post post = posts.get(0);
        assertThat(post.getMember()).isEqualTo(getMember());
        assertThat(getMember().getPosts().get(0)).isEqualTo(post);

        // 업로드 파일 검증
        List<ImageFile> imageFiles = Optional.ofNullable(imageFileRepository.findAll())
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage());
                });

        assertThat(imageFiles).hasAtLeastOneElementOfType(ImageFile.class);

        // post - images 연관관계 검 증
        assertThat(imageFiles).usingRecursiveFieldByFieldElementComparator()
                .extracting("post")
                .contains(post);
    }

    @DisplayName("로그인 유저와 글쓴이가 같지 않으면 예외를 던진다")
    @Test
    @Sql(scripts = "classpath:db/test/postAndImages.sql")
    void throwExceptionWhenDeletePost() throws Exception {

        //Arrange
        Long pid = 1l;

        UserDto userDto = new UserDto(getMember());

        String username = "username1";

        //Act
        mockMvc.perform(delete("/posts/{pid}", pid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDto))
                .param("username", username))
                .andExpect(result -> assertThat(result.getResolvedException() instanceof
                        InvalidValueException).isTrue());

        //Assert

    }

    @Test
    @Sql(scripts = "classpath:db/test/postAndImages.sql")
    void deletePost() throws Exception {
        //Arrange
        Long pid = 1l;

        UserDto userDto = new UserDto(getMember());

        String usename = "username";
        //Act
        mockMvc.perform(delete("/posts/{pid}", pid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDto))
                .param("username", usename))
                .andDo(print())
                .andExpect(authenticated());

        //Assert
        List<Post> posts = postRepository.findAll();

        assertThat(posts.size()).isEqualTo(0);

        List<ImageFile> imageFiles = imageFileRepository.findAll();

        assertThat(imageFiles.size()).isEqualTo(0);
    }
}