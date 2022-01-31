package com.example.studywithme.post.api;

import com.example.studywithme.global.auth.UserDto;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.matchesRegex;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
@SpringBootTest
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
public class PostApiAcceptanceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ImageFileRepository imageFileRepository;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    private List<Post> createPosts() {
        List<Post> posts = new ArrayList<>();

        for (int i = 0; i < 33; i++) {
            Post post = Post.builder()
                    .title("title" + i)
                    .content("content" + i)
                    .hits(0L)
                    .likeCounts(0L)
                    .build();

            Post save = postRepository.save(post);
            posts.add(save);
        }
        return posts;
    }

    private Post createPost() {
        Post post = Post.builder().title("title")
                .content("content")
                .hits(0l)
                .likeCounts(0l)
                .build();

        return postRepository.save(post);
    }

    private Member createMember() {
        Member member = Member.builder().username("username")
                .password(new BCryptPasswordEncoder().encode("password"))
                .nickname("nickname")
                .role("ROLE_USER")
                .build();

        return memberRepository.save(member);
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

    private ImageFile createImageFile() {
        ImageFile imageFile = ImageFile.builder().path("path")
                .build();

        return imageFileRepository.save(imageFile);
    }

    @DisplayName("로그인이 되어 있으면 글을 쓸 수 있다")
    @Test
    void writePost() throws Exception {

        //Arrange
        Member member = createMember();
        String title = "title";
        String content = "content";
        UserDto userDto = new UserDto(member);

        MockMultipartFile postRequestMultipart = getPostRequestMultipart(title, content);

        MockMultipartFile multipartFile1 = getMultipartFile("hello1.txt", "hello world1");

        MockMultipartFile multipartFile2 = getMultipartFile("hello2.txt", "hello world2");

        //Act
        mockMvc.perform(multipart("/posts")
                .file(postRequestMultipart)
                .file(multipartFile1)
                .file(multipartFile2)
                .with(user(userDto))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(authenticated())
                .andDo(print());

        //Assert
        List<Post> posts = postRepository.findAll();
        List<ImageFile> imageFiles = imageFileRepository.findAll();

        assertThat(posts).usingRecursiveFieldByFieldElementComparator()
                .extracting("title", "content", "likeCounts", "hits", "member", "imageFiles")
                .contains(tuple(title, content, 0L, 0L, member, imageFiles));
    }

    @Test
    void readPost() throws Exception {
        //Arrange
        Post post = createPost();
        Long pid = post.getPid();

        Member member = createMember();
        member.updatePost(post);

        ImageFile imageFile = createImageFile();
        post.updateImageFile(imageFile);

        //Act
        MockHttpServletResponse response = mockMvc.perform(get("/posts/{pid}", pid))
                .andDo(print())
                .andReturn().getResponse();

        //Assert
        PostResponse postResponse = objectMapper.readValue(response.getContentAsString(), PostResponse.class);

        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(postResponse));
        assertThat(postResponse.getPid()).isEqualTo(pid);
        assertThat(postResponse.getTitle()).isEqualTo("title");
        assertThat(postResponse.getNickname()).isEqualTo("nickname");
        assertThat(postResponse.getImageFiles()).usingRecursiveFieldByFieldElementComparator()
                .extracting("path")
                .containsAll(Arrays.asList("path"));
        assertThat(postResponse.getHits()).isEqualTo(1l);
    }

    @DisplayName("로그인 유저와 글쓴이가 같지 않으면 예외를 던진다")
    @Test
    void throwExceptionWhenModifyPost() throws Exception {
        //Arrange
        Member member = createMember();
        UserDto userDto = new UserDto(member);
        String username = "username1";

        Post post = createPost();
        Long pid = post.getPid();

        String modified_title = "modified title";
        String modified_content = "modified content";
        MockMultipartFile postRequestMultipart = getPostRequestMultipart(modified_title, modified_content);

        String originalName = "modified_hello1.txt";
        String contentName = "modified hello world1";
        MockMultipartFile multipartFile = getMultipartFile(originalName, contentName);

        String originalName2 = "modified_hello2.txt";
        String contentName2 = "modified hello world2";
        MockMultipartFile multipartFile1 = getMultipartFile(originalName2, contentName2);

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
    void modifyPost() throws Exception {
        //Arrange
        Member member = createMember();
        UserDto userDto = new UserDto(member);
        String username = member.getUsername();

        Post post = createPost();
        Long pid = post.getPid();

        String modified_title = "modified title";
        String modified_content = "modified content";
        MockMultipartFile postRequestMultipart = getPostRequestMultipart(modified_title, modified_content);

        String originalName = "modified_hello1.txt";
        String contentName = "modified hello world1";
        MockMultipartFile multipartFile = getMultipartFile(originalName, contentName);

        String originalName2 = "modified_hello2.txt";
        String contentName2 = "modified hello world2";
        MockMultipartFile multipartFile1 = getMultipartFile(originalName2, contentName2);

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
        List<ImageFile> imageFiles = imageFileRepository.findAll();

        assertThat(posts).usingRecursiveFieldByFieldElementComparator()
                .extracting("title", "content", "imageFiles")
                .contains(tuple(modified_title, modified_content, imageFiles));
    }

    @DisplayName("로그인 유저와 글쓴이가 같지 않으면 예외를 던진다")
    @Test
    void throwExceptionWhenDeletePost() throws Exception {

        //Arrange
        Member member = createMember();
        UserDto userDto = new UserDto(member);

        String username = "username1";

        Post post = createPost();
        Long pid = post.getPid();

        //Act
        mockMvc.perform(delete("/posts/{pid}", pid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDto))
                .param("username", username))
                .andExpect(result -> assertThat(result.getResolvedException() instanceof
                        InvalidValueException).isTrue());
    }

    @Test
    void deletePost() throws Exception {
        //Arrange
        Member member = createMember();
        UserDto userDto = new UserDto(member);
        String username = member.getUsername();

        Post post = createPost();
        Long pid = post.getPid();

        //Act
        mockMvc.perform(delete("/posts/{pid}", pid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDto))
                .param("username", username))
                .andDo(print())
                .andExpect(authenticated());

        //Assert
        boolean present = postRepository.findById(pid).isPresent();
        assertThat(present).isFalse();
    }

    @DisplayName("검색어 없이 모든 게시물을 조회한다")
    @Test
    void readPosts() throws Exception {
        //Arrange
        List<Post> posts = createPosts();

        Member member = createMember();

        for (Post post : posts) {
            member.updatePost(post);
        }

        //Act
        mockMvc.perform(get("/posts"))
                .andDo(print())
                .andExpect(jsonPath("$['number']").value(0))
                .andExpect(jsonPath("$['size']").value(10))
                .andExpect(jsonPath("$['totalElements']").value(33))
                .andExpect(jsonPath("$['sort']['sorted']").value(true));
    }

    @DisplayName("검색으로 게시물을 조회한다")
    @Test
    void read_posts_using_search() throws Exception {
        //Arrange
        Post post = createPost();

        String search = post.getTitle();
        Member member = createMember();

        member.updatePost(post);

        //Act
        mockMvc.perform(get("/posts")
                .param("search", search))
                .andDo(print())
                .andExpect(jsonPath("$['number']").value(0))
                .andExpect(jsonPath("$['size']").value(10))
                .andExpect(jsonPath("$['totalElements']").value(1))
                .andExpect(jsonPath("$['sort']['sorted']").value(true))
                .andExpect(jsonPath("$..['title']").value(search));
    }

    @DisplayName("검색을 했을 때 게시물이 존재하지 않는다")
    @Test
    void read_zero_posts_using_search() throws Exception {
        //Arrange
        Post post = createPost();

        String search = "abcd";
        Member member = createMember();

        member.updatePost(post);

        //Act
        mockMvc.perform(get("/posts")
                .param("search", search))
                .andDo(print())
                .andExpect(jsonPath("$['number']").value(0))
                .andExpect(jsonPath("$['totalElements']").value(0))
                .andExpect(jsonPath("$..content.length()").value(0));
    }

    @DisplayName("내가 쓴 게시물을 조회한다")
    @Test
    void readMyPosts() throws Exception {
        //Arrange
        List<Post> posts = createPosts();

        Member member = createMember();
        UserDto userDto = new UserDto(member);

        for (Post post : posts) {
            member.updatePost(post);
        }

        //Act
        mockMvc.perform(get("/posts/members")
                .with(user(userDto)))
                .andDo(print())
                .andExpect(jsonPath("$..number").value(0))
                .andExpect(jsonPath("$..size").value(10))
                .andExpect(jsonPath("$..totalElements").value(33))
                .andExpect(jsonPath("$['sort']['sorted']").value(true))
                .andExpect(jsonPath("$..content").isNotEmpty())
                .andExpect(jsonPath("$..username", hasItem(member.getUsername())))
                .andExpect(jsonPath("$..title", hasItem(matchesRegex("^(title).*$"))));
    }
}