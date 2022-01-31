package com.example.studywithme.reply.interact;

import com.example.studywithme.global.auth.UserDto;
import com.example.studywithme.member.application.entity.Member;
import com.example.studywithme.post.application.entity.Post;
import com.example.studywithme.post.application.interact.PostService;
import com.example.studywithme.reply.application.dao.ReplyRepository;
import com.example.studywithme.reply.application.dto.ReplyRequest;
import com.example.studywithme.reply.application.dto.ReplyResponse;
import com.example.studywithme.reply.application.entity.Reply;
import com.example.studywithme.reply.application.interact.impl.ReplyServiceImpl;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ReplyServiceTest {
    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private PostService postService;

    @InjectMocks
    private ReplyServiceImpl replyService;

    private Post createPost() {
        return Post.builder().title("title")
                .content("content")
                .likeCounts(0l)
                .hits(0l)
                .build();
    }

    private Member createMember() {
        return Member.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .role("USER_ROLE")
                .build();
    }

    private Reply createReply() {
        return Reply.builder().content("content").build();
    }

    @DisplayName("댓글을 등록한다")
    @Test
    void writeReply() {
        //Arrange
        Post post = createPost();
        Long pid = post.getPid();

        Member member = createMember();
        com.example.studywithme.global.auth.UserDto userDto = new com.example.studywithme.global.auth.UserDto(member);

        ReplyRequest replyRequest = new ReplyRequest("content");

        given(postService.getPost(pid)).willReturn(post);

        //Act
        replyService.writeReply(userDto, pid, replyRequest);

        //Assert
        assertThat(post.getReplies().size()).isEqualTo(1);
        assertThat(member.getReplies().size()).isEqualTo(1);

        verify(replyRepository, times(1)).save(any());
    }

    @DisplayName("댓글을 수정한다")
    @Test
    void modifyReply() {
        //Arrange
        Long rid = 1l;

        String modified_content = "modified_content";
        ReplyRequest replyRequest = new ReplyRequest(modified_content);

        Reply reply = createReply();
        given(replyRepository.findById(rid)).willReturn(Optional.ofNullable(reply));

        //Act
        replyService.modifyReply(rid, replyRequest);

        //Assert
        verify(replyRepository, times(1)).findById(rid);
        assertThat(reply.getContent()).isEqualTo(modified_content);
    }

    @DisplayName("댓글을 삭제한다")
    @Test
    void deleteReply() {
        //Arrange
        Reply reply = createReply();
        Long rid = reply.getRid();

        //Act
        replyService.deleteReply(rid);

        //Assert
        verify(replyRepository, times(1)).deleteById(any());
    }

    @DisplayName("특정 게시물에 모든 댓글을 조회한다")
    @Test
    void readReplies() {
        //Arrange

        Long pid = 1l;
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdDate");

        //Act
        Page<ReplyResponse> replyResponses = replyService.readReplies(pid, pageable);

        //Assert
        verify(replyRepository, times(1)).findAllByPid(pid);
        assertThat(replyResponses.getNumber()).isEqualTo(0);
        assertThat(replyResponses.getSize()).isEqualTo(10);
        assertThat(replyResponses.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdDate"));
    }

    @Test
    void readMyReplies() {
        //Arrange
        Member member = createMember();
        UserDto userDto = new UserDto(member);

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdDate");

        //Act
        replyService.readMyReplies(userDto, pageable);

        //Assert
        verify(replyRepository, times(1)).findAllByMember(member);
    }
}