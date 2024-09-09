package com.example.baseball.service;

import com.example.baseball.domain.Member;
import com.example.baseball.domain.Post;
import com.example.baseball.domain.Team;
import com.example.baseball.dto.CommentDto;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void 게시물_댓글_작성() throws Exception {
        //given
        Team team = Team.builder()
                .teamId(UUID.randomUUID().toString())
                .teamName("SSG")
                .build();
        entityManager.persist(team);

        Member member = Member.builder()
                .memberId(UUID.randomUUID().toString())
                .email("yongjung95@gmail.com")
                .nickname("정이")
                .password("123456")
                .followedTeam(team)
                .build();
        entityManager.persist(member);

        Member member2 = Member.builder()
                .memberId(UUID.randomUUID().toString())
                .email("yongjung952@gmail.com")
                .nickname("정이2")
                .password("123456")
                .followedTeam(team)
                .build();
        entityManager.persist(member2);

        Post post = Post.builder()
                .title("게시글 테스트")
                .content("내용")
                .author(member)
                .followedTeam(team)
                .build();

        entityManager.persist(post);

        entityManager.flush();
        entityManager.clear();

        //when
        CommentDto.SaveCommentDto dto = new CommentDto.SaveCommentDto();
        dto.setPostId(post.getPostId());
        dto.setContent("댓글 테스트");
        dto.setAuthorId(member.getMemberId());
        CommentDto.SendTopicCommentDto result = commentService.saveComment(dto);
        //then
        assertThat(result.getPostId()).isEqualTo(post.getPostId());
    }
}