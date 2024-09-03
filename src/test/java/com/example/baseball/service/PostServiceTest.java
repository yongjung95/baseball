package com.example.baseball.service;

import com.example.baseball.domain.Comment;
import com.example.baseball.domain.Member;
import com.example.baseball.domain.Post;
import com.example.baseball.domain.Team;
import com.example.baseball.dto.CommentDto;
import com.example.baseball.dto.PostDto;
import com.example.baseball.repository.CommentRepository;
import com.example.baseball.repository.PostRepository;
import com.example.baseball.response.exception.ApiException;
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
class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    void 게시물_생성() throws Exception {
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

        PostDto.SavePostRequestDto dto = new PostDto.SavePostRequestDto();
        dto.setTitle("게시글 테스트");
        dto.setContent("게시글 테스트");
        dto.setAuthorId(member.getMemberId());

        //when
        PostDto.ResponsePostDto responsePostDto = postService.savePost(dto);

        //then
        assertThat(responsePostDto.getTitle()).isEqualTo(dto.getTitle());
    }

    @Test
    void 게시물_생성_팀_에러_발생() throws Exception {
        //given
        Member member = Member.builder()
                .memberId(UUID.randomUUID().toString())
                .email("yongjung95@gmail.com")
                .nickname("정이")
                .password("123456")
                .build();
        entityManager.persist(member);

        //when
        PostDto.SavePostRequestDto dto = new PostDto.SavePostRequestDto();
        dto.setTitle("게시글 테스트");
        dto.setContent("게시글 테스트");
        dto.setAuthorId(member.getMemberId());

        //then
        Assertions.assertThatThrownBy(() -> postService.savePost(dto)).isInstanceOf(ApiException.class);
    }

    @Test
    void 게시물_생성_회원_에러_발생() throws Exception {
        //given
        Team team = Team.builder()
                .teamId(UUID.randomUUID().toString())
                .teamName("SSG")
                .build();
        entityManager.persist(team);

        //when
        PostDto.SavePostRequestDto dto = new PostDto.SavePostRequestDto();
        dto.setTitle("게시글 테스트");
        dto.setContent("게시글 테스트");
        dto.setAuthorId("@@@@@");

        //then
        Assertions.assertThatThrownBy(() -> postService.savePost(dto)).isInstanceOf(ApiException.class);
    }

    @Test
    void 게시물_삭제() throws Exception {
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
                .title("게시글 테스트")
                .content("내용")
                .author(member)
                .followedTeam(team)
                .build();

        entityManager.persist(post);
        entityManager.flush();
        entityManager.clear();

        //when
        PostDto.UpdatePostRequestDto dto = new PostDto.UpdatePostRequestDto();
        dto.setPostId(post.getPostId());
        dto.setAuthorId(member.getMemberId());

        //then
        postService.deletePost(dto);

        Post findPost = postRepository.findByPostId(post.getPostId());
        assertThat(findPost.isUse()).isFalse();
    }

    @Test
    void 게시물_수정() throws Exception {
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
                .title("게시글 테스트")
                .content("내용")
                .author(member)
                .followedTeam(team)
                .build();

        entityManager.persist(post);
        entityManager.flush();
        entityManager.clear();

        //when
        PostDto.UpdatePostRequestDto dto = new PostDto.UpdatePostRequestDto();
        dto.setTitle("게시글 변경테스트");
        dto.setPostId(post.getPostId());
        dto.setAuthorId(member.getMemberId());

        //then
        PostDto.ResponsePostDto result = postService.updatePost(dto);

        assertThat(result.getTitle()).isEqualTo(dto.getTitle());
    }

    @Test
    void 게시글_목록_조회() throws Exception {
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
                .title("게시글 테스트")
                .content("내용")
                .author(member)
                .followedTeam(team)
                .build();
        Post post2 = Post.builder()
                .title("게시글 테스트2")
                .content("내용2")
                .author(member)
                .followedTeam(team)
                .build();

        entityManager.persist(post);
        entityManager.persist(post2);
        entityManager.flush();
        entityManager.clear();

        //when
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdDate"));
        String searchText = "게시글";

        //then
        Page<PostDto.ResponsePostDto> result = postService.selectPostList(searchText, team.getTeamId(), pageRequest);
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0).getTeamName()).isEqualTo("SSG");
    }

    @Test
    void 게시글_상세_조회() throws Exception {
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
                .title("게시글 테스트")
                .content("내용")
                .author(member)
                .followedTeam(team)
                .build();

        entityManager.persist(post);
        entityManager.flush();
        entityManager.clear();

        //when
        PostDto.ResponsePostDto resultDto = postService.selectPost(post.getPostId());

        //then
        assertThat(resultDto.getTitle()).isEqualTo(post.getTitle());
    }

    @Test
    void 게시물_좋아요() throws Exception {
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

        Member member1 = Member.builder()
                .memberId(UUID.randomUUID().toString())
                .email("yongjung95@naver.com")
                .nickname("랜더스팬")
                .password("123456")
                .followedTeam(team)
                .build();
        entityManager.persist(member1);

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
        boolean result = postService.savePostLike(member1.getMemberId(), post.getPostId());

        //then
        assertThat(result).isTrue();
    }

    @Test
    void 게시물_좋아요_본인은_불가() throws Exception {
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
                .title("게시글 테스트")
                .content("내용")
                .author(member)
                .followedTeam(team)
                .build();

        entityManager.persist(post);
        entityManager.flush();
        entityManager.clear();

        //when
        String memberId = member.getMemberId();
        Long postId = post.getPostId();

        //then
        Assertions.assertThatThrownBy(() -> postService.savePostLike(memberId, postId)).isInstanceOf(ApiException.class);
    }

    @Test
    void 게시물_댓글_조회() throws Exception {
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

        Member member1 = Member.builder()
                .memberId(UUID.randomUUID().toString())
                .email("yongjung95@naver.com")
                .nickname("랜더스팬")
                .password("123456")
                .followedTeam(team)
                .build();
        entityManager.persist(member1);

        Post post = Post.builder()
                .title("게시글 테스트")
                .content("내용")
                .author(member)
                .followedTeam(team)
                .build();
        entityManager.persist(post);

        Comment comment = Comment.builder()
                .post(post)
                .author(member)
                .content("댓글1")
                .parent(null)
                .build();
        entityManager.persist(comment);

        Comment comment3 = Comment.builder()
                .post(post)
                .author(member)
                .content("댓글1-1")
                .parent(comment)
                .build();
        entityManager.persist(comment3);

        Comment comment2 = Comment.builder()
                .post(post)
                .author(member)
                .content("댓글2")
                .parent(null)
                .build();
        entityManager.persist(comment2);

        entityManager.flush();
        entityManager.clear();

        //when
        PostDto.ResponsePostDto result = postService.selectPost(post.getPostId());

        //then
        assertThat(result.getComments().size()).isEqualTo(2);

        for (CommentDto.ResponseCommentDto resultComment : result.getComments()) {
            System.out.println("resultComment = " + resultComment);
        }
    }
}