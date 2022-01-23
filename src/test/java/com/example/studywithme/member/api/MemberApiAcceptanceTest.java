package com.example.studywithme.member.api;

import com.example.studywithme.global.error.exception.BusinessException;
import com.example.studywithme.member.application.dao.MemberRepository;
import com.example.studywithme.member.application.dto.MemberRequest;
import com.example.studywithme.member.application.entity.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("application-test.yml")
public class MemberApiAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private Member createMember() {
        Member member = Member.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .role("ROLE_USER")
                .build();

        return memberRepository.save(member);
    }

    @DisplayName("데이터베이스에 똑같은 아이디 혹은 닉네임이 있으면 예외를 던진다")
    @Test
    void throw_exception_when_member_already_has_register_data() throws Exception {
        //Arrange
        createMember();

        String username = "username";
        String password = "passwd1234!@#$";
        String nickname = "nickname";

        MemberRequest memberRequest = new MemberRequest(username, password, nickname);

        //Act
        mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(unauthenticated())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof
                        BusinessException).isTrue())
                .andDo(print());
        //Assert

    }

    @Test
    void register() throws Exception {
        //Arrange

        String username = "username";
        String password = "passwd1234!@#$";
        String nickname = "nickname";

        MemberRequest memberRequest = new MemberRequest(username, password, nickname);

        //Act
        mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(unauthenticated())
                .andDo(print());

        //Assert

        List<Member> members = memberRepository.findAll();

        assertThat(members).usingRecursiveFieldByFieldElementComparator()
                .extracting("username", "nickname", "role")
                .contains(tuple(username, nickname, "ROLE_USER"));
    }

    @Test
    void readInfo() throws Exception {
        //Arrange
        Member member = createMember();

        Long mid = member.getMid();

        //Act
        mockMvc.perform(get("/members/{mid}", mid)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$['username']").value(member.getUsername()))
                .andExpect(jsonPath("$['nickname']").value(member.getNickname()));
        //Assert
    }

    @DisplayName("데이터베이스에 똑같은 닉네임이 있으면 예외를 던진다")
    @Test
    void throw_exception_when_member_already_has_modify_data() throws Exception {
        //Arrange
        Member member = createMember();
        Long mid = member.getMid();

        String username = "username";
        String password = "passwd1234!@#$";
        String nickname = "nickname";
        MemberRequest memberRequest = new MemberRequest(username, password, nickname);

        //Act
        mockMvc.perform(put("/members/{mid}", mid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(unauthenticated())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof
                        BusinessException).isTrue())
                .andDo(print());

        //Assert

    }

    @Test
    void modifyInfo() throws Exception {
        //Arrange
        Member member = createMember();
        Long mid = member.getMid();

        String username = "username";
        String password = "passwd1234!@#$";
        String modified_nickname = "modified_nickname";
        MemberRequest memberRequest = new MemberRequest(username, password, modified_nickname);

        //Act
        mockMvc.perform(put("/members/{mid}", mid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequest)))
                .andDo(print())
                .andExpect(unauthenticated());

        //Assert

        Member expect = memberRepository.findById(mid).get();

        assertThat(expect.getNickname()).isEqualTo(modified_nickname);
        assertThat(expect.getUsername()).isEqualTo(username);
    }
}
