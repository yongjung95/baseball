package com.example.baseball.repository;

import com.example.baseball.domain.Member;
import com.example.baseball.domain.Post;
import com.example.baseball.domain.PostLike;
import com.example.baseball.domain.Team;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@Transactional
class PostLikeRepositoryTest {

    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void 게시글_좋아요_테스트() throws Exception {
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

        entityManager.persist(post);

        String teamId = team.getTeamId();


        //when
        PostLike postLike = PostLike.builder()
                .member(member)
                .post(post)
                .build();

        postLikeRepository.save(postLike);

        entityManager.flush();
        entityManager.clear();

        //then
        PostLike result = postLikeRepository.findByMemberAndPost(member, post);

        Assertions.assertThat(result).isNotNull();
    }
}