package com.example.baseball.service;

import com.example.baseball.domain.Member;
import com.example.baseball.domain.Post;
import com.example.baseball.dto.PostDto;
import com.example.baseball.repository.MemberRepository;
import com.example.baseball.repository.PostRepository;
import com.example.baseball.response.error.ApiException;
import com.example.baseball.response.error.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public PostDto.ResponsePostDto savePost(PostDto.SavePostRequestDto dto) {
        Member member = memberRepository.findByMemberId(dto.getAuthorId());
        if (member == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }
        if (member.getFollowedTeam() == null) {
            throw new ApiException(ErrorCode.POST_CREATE_IS_ONLY_SELECTED_TEAM);
        }

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(member)
                .followedTeam(member.getFollowedTeam())
                .build();

        postRepository.save(post);
        PostDto.ResponsePostDto result = modelMapper.map(post, PostDto.ResponsePostDto.class);
        result.setTeamName(member.getFollowedTeam().getTeamName());
        result.setAuthorNickname(member.getNickname());

        return result;
    }

    public void deletePost(PostDto.UpdatePostRequestDto dto) {
        Member member = memberRepository.findByMemberId(dto.getAuthorId());
        if (member == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        Post post = postRepository.findByPostId(dto.getPostId());
        if (post == null) {
            throw new ApiException(ErrorCode.POST_IS_NOT_FOUND);
        }

        if (!post.getAuthor().getMemberId().equals(dto.getAuthorId())) {
            throw new ApiException(ErrorCode.AUTHOR_IS_NOT_MATCHED);
        }

        post.changeIsUse();
    }

    public PostDto.ResponsePostDto updatePost(PostDto.UpdatePostRequestDto dto) {
        Member member = memberRepository.findByMemberId(dto.getAuthorId());
        if (member == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        Post post = postRepository.findByPostId(dto.getPostId());
        if (post == null) {
            throw new ApiException(ErrorCode.POST_IS_NOT_FOUND);
        }

        if (!post.getAuthor().getMemberId().equals(dto.getAuthorId())) {
            throw new ApiException(ErrorCode.AUTHOR_IS_NOT_MATCHED);
        }

        post.updatePost(dto.getTitle(), dto.getContent());
        PostDto.ResponsePostDto result = modelMapper.map(post, PostDto.ResponsePostDto.class);
        result.setAuthorNickname(post.getAuthor().getNickname());
        result.setTeamName(post.getFollowedTeam().getTeamName());

        return result;
    }

    public Page<PostDto.ResponsePostDto> selectPostList(String searchText, String teamId, Pageable pageable) {
        Page<Post> postList = postRepository.selectPostListByTeam(searchText, teamId, pageable);

        List<PostDto.ResponsePostDto> result = postList.stream().map(this::convertToDto)
                .toList();
        return new PageImpl<>(result, pageable, postList.getTotalElements());
    }

    public PostDto.ResponsePostDto selectPost(Long postId) {
        Post post = postRepository.findByPostId(postId);

        return convertToDto(post);
    }

    private PostDto.ResponsePostDto convertToDto(Post post) {
        PostDto.ResponsePostDto responsePostDto = modelMapper.map(post, PostDto.ResponsePostDto.class);
        responsePostDto.setAuthorNickname(post.getAuthor().getNickname());
        responsePostDto.setTeamName(post.getFollowedTeam().getTeamName());
        responsePostDto.setCreateDate(formatTimeAgo(post.getCreatedDate()));
        return responsePostDto;
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "방금 전";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + "분 전";
        } else if (seconds < 86400) { // 24 * 60 * 60
            long hours = seconds / 3600;
            return hours + "시간 전";
        } else {
            long days = seconds / 86400;
            return days + "일 전";
        }
    }
}
