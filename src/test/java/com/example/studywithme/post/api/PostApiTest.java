package com.example.studywithme.post.api;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.global.error.exception.EntityNotFoundException;
import com.example.studywithme.global.error.exception.ErrorCode;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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

    @Test
    @Sql(scripts = "classpath:db/test/member.sql")
    void should_create_post_if_member_is_logged_in() throws Exception {

        //Arrange

        MockMultipartFile postRequestFile = getMockMultipartFile();

        MockMultipartFile multipartFile1 = getMultipartFile("hello1.txt", "hello world1");

        MockMultipartFile multipartFile2 = getMultipartFile("hello2.txt", "hello world2");

        UserDto userDto = new UserDto(getMember());

        //Act
        mockMvc.perform(multipart("/posts")
                .file(postRequestFile)
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
                .extracting("title", "content", "likeCounts", "hits")
                .contains(tuple("title", "content", 0L, 0L));

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

    private Member getMember() {
        Member member = Optional.ofNullable(memberRepository.findById(1l))
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage());
                }).get();
        return member;
    }

    private MockMultipartFile getMultipartFile(String originalName, String contentName) {
        return new MockMultipartFile(
                "multipartFiles", "hello1.txt",
                MediaType.MULTIPART_FORM_DATA_VALUE, "hello world1".getBytes()
        );
    }

    private MockMultipartFile getMockMultipartFile() throws JsonProcessingException {
        PostRequest postRequest = new PostRequest("title", "content");

        MockMultipartFile postRequestFile = new MockMultipartFile("postRequest",
                "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(postRequest).getBytes(StandardCharsets.UTF_8));
        return postRequestFile;
    }

    @Test
    @Sql(scripts = "classpath:db/test/post_associated_images.sql")
    void should_read_post_and_associated_image_files() throws Exception {
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
}