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
import org.springframework.stereotype.Service;

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
}
