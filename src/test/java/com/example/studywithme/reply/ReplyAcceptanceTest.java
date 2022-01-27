package com.example.studywithme.reply;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.member.application.dao.MemberRepository;
import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.post.application.dao.PostRepository;
import com.example.studywithme.post.application.entity.Post;
import com.example.studywithme.reply.application.dao.ReplyRepository;
import com.example.studywithme.reply.application.dto.ReplyRequest;
import com.example.studywithme.reply.application.entity.Reply;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ReplyAcceptanceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void writeReply() throws Exception {
        //Arrange
        Member member = createMember();
        UserDto userDto = new UserDto(member);

        Post post = createPost();
        Long pid = post.getPid();

        String content = "reply content";
        ReplyRequest replyRequest = new ReplyRequest(content);

        //Act
        mockMvc.perform(post("/replies")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDto))
                .param("pid", String.valueOf(pid))
                .content(objectMapper.writeValueAsString(replyRequest)))
                .andDo(print());

        //Assert
        List<Reply> replies = replyRepository.findAll();
        assertThat(replies).usingRecursiveFieldByFieldElementComparator()
                .extracting("content")
                .contains(content);

        assertThat(replies).usingRecursiveFieldByFieldElementComparator()
                .extracting("member")
                .contains(member);

        assertThat(replies).usingRecursiveFieldByFieldElementComparator()
                .extracting("post")
                .contains(post);
    }

    @Test
    void modifyReply() throws Exception {

        //Arrange
        Reply reply = createReply();
        Long rid = reply.getRid();

        String modified_content = "modified_content";
        ReplyRequest replyRequest = new ReplyRequest(modified_content);

        Member member = createMember();
        UserDto userDto = new UserDto(member);
        String username = member.getUsername();

        //Act
        mockMvc.perform(put("/replies/{rid}", rid)
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDto))
                .content(objectMapper.writeValueAsString(replyRequest)))
                .andExpect(authenticated())
                .andDo(print());

        //Assert
        List<Reply> replies = replyRepository.findAll();

        assertThat(replies).usingRecursiveFieldByFieldElementComparator()
                .extracting("content")
                .contains(modified_content);
    }

    @Test
    void deleteReply() throws Exception {
        //Arrange
        Reply reply = createReply();

        Long rid = reply.getRid();
        Member member = createMember();
        UserDto userDto = new UserDto(member);
        String username = member.getUsername();

        //Act
        mockMvc.perform(delete("/replies/{rid}", rid)
                .param("username", username)
                .with(user(userDto)))
                .andExpect(authenticated())
                .andDo(print());

        //Assert
        boolean present = replyRepository.findById(rid).isPresent();
        assertThat(present).isFalse();
    }

    @Test
    void readReplies() throws Exception {
        //Arrange
        Post post = createPost();
        Long pid = post.getPid();

        Member member = createMember();

        List<Reply> replies = createReplies();

        for (Reply reply : replies) {
            post.updateReply(reply);
            member.updateReply(reply);
        }

        //Act
        mockMvc.perform(get("/replies")
                .param("pid", String.valueOf(pid)))
                .andDo(print())
                .andExpect(jsonPath("$['number']").value(0))
                .andExpect(jsonPath("$['size']").value(10))
                .andExpect(jsonPath("$['totalElements']").value(33))
                .andExpect(jsonPath("$['sort']['sorted']").value(true))
                .andExpect(jsonPath("$..mid").isNotEmpty())
                .andExpect(jsonPath("$..pid").isNotEmpty());
    }

    private List<Reply> createReplies() {
        List<Reply> replies = new ArrayList<>();

        for (int i = 0; i < 33; i++) {
            Reply reply = Reply.builder().content("reply content" + i).build();
            Reply save = replyRepository.save(reply);
            replies.add(save);
        }

        return replies;
    }

    private Reply createReply() {
        Reply reply = Reply.builder()
                .content("content")
                .build();

        return replyRepository.save(reply);
    }

    private Post createPost() {
        Post post = Post.builder()
                .title("title")
                .content("content")
                .likeCounts(0l)
                .hits(0l)
                .build();

        return postRepository.save(post);
    }

    private Member createMember() {
        Member member = Member.builder()
                .username("username")
                .password("password1234!@#$")
                .nickname("nickname")
                .role("ROLE_USER")
                .build();

        return memberRepository.save(member);
    }
}
