package com.example.studywithme.global.auth;

import com.example.studywithme.member.application.dao.MemberRepository;
import com.example.studywithme.member.application.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void login() throws Exception {

        //Arrange
        Member member = createMember();
        String username = member.getUsername();
        String password = member.getPassword();

        //Act
        mockMvc.perform(post("/login")
                .param("username", username)
                .param("password", password))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        Member member1 = memberRepository.findByUsername(member.getUsername()).orElseThrow();
        System.out.println("login date : " + member1.getLoginDate());
    }

    private Member createMember() {
        Member member = Member.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .role("USER_ROLE")
                .build();

        return memberRepository.save(member);
    }

    @Test
    void logout() throws Exception {

        //Act
        mockMvc.perform(post("/logout"))
                .andExpect(status().is3xxRedirection());
    }
}
