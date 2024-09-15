package com.example.baseball.repository;

import com.example.baseball.domain.Member;
import com.example.baseball.domain.Post;
import com.example.baseball.domain.Team;
import com.example.baseball.dto.PostDto;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void 게시글_생성_및_목록_조회() throws Exception {
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

        Post post = Post.builder()
                .title("작성글")
                .content("내용")
                .author(member)
                .followedTeam(team)
                .build();

        postRepository.save(post);

        String teamId = team.getTeamId();
        entityManager.flush();
        entityManager.clear();

        //when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "createdDate"));

        Page<PostDto.ResponsePostDto> responsePostDtos = postRepository.selectPostListByTeam("작성글", teamId, pageRequest);

        //then
        assertThat(responsePostDtos.getTotalElements()).isEqualTo(1);
    }
}