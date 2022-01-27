package com.example.studywithme.reply.interact;

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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ReplyServiceTest {
    @Mock
    private ReplyRepository replyRepository;

    @InjectMocks
    private ReplyServiceImpl replyService;

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

    private Reply createReply() {
        return Reply.builder().content("content").build();
    }
}
