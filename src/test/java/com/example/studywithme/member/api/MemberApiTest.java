package com.example.studywithme.member.api;

import com.example.studywithme.member.application.dao.MemberRepository;
import com.example.studywithme.member.application.dto.MemberRequest;
import com.example.studywithme.member.application.dto.MemberResponse;
import com.example.studywithme.member.application.entity.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("application-test.yml")
public class MemberApiTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void register() throws Exception {
        //Arrange

        String username = "username";
        String password = "password";
        String nickname = "nickname";

        MemberRequest memberRequest = new MemberRequest(username, password, nickname);

        //Act
        mockMvc.perform(post("/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequest)))
                .andDo(print())
                .andExpect(unauthenticated());

        //Assert

        List<Member> members = memberRepository.findAll();

        assertThat(members).usingRecursiveFieldByFieldElementComparator()
                .extracting("username", "nickname", "role")
                .contains(tuple(username, nickname, "ROLE_USER"));
    }

    @Test
    @Sql(scripts = "classpath:db/test/member.sql")
    void readInfo() throws Exception {
        //Arrange
        String username = "username1";

        //Act
        MockHttpServletResponse response = mockMvc.perform(get("/members/{username}", username)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //Assert

        Member member = memberRepository.findByUsername(username).get();

        MemberResponse memberResponse = objectMapper.readValue(response.getContentAsString(), MemberResponse.class);

        assertThat(memberResponse.getUsername()).isEqualTo(member.getUsername());
        assertThat(memberResponse.getNickname()).isEqualTo(member.getNickname());
    }


}
