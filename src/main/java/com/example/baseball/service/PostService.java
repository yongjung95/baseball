package com.example.baseball.service;

import com.example.baseball.domain.Comment;
import com.example.baseball.domain.Member;
import com.example.baseball.domain.Post;
import com.example.baseball.domain.PostLike;
import com.example.baseball.dto.AttachmentFileDto;
import com.example.baseball.dto.CommentDto;
import com.example.baseball.dto.PostDto;
import com.example.baseball.repository.CommentRepository;
import com.example.baseball.repository.MemberRepository;
import com.example.baseball.repository.PostLikeRepository;
import com.example.baseball.repository.PostRepository;
import com.example.baseball.response.error.ErrorCode;
import com.example.baseball.response.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;
    private final AttachmentFileService attachmentFileService;

    public PostDto.ResponsePostDto savePost(PostDto.SavePostRequestDto dto) throws IOException {
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

        if (!dto.getFiles().isEmpty()) {
            List<AttachmentFileDto.SelectAttachmentFileDto> files = attachmentFileService.saveAttachmentFile(dto.getFiles(), member, post);
            result.setFiles(files);
        }

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
        Page<PostDto.ResponsePostDto> result = postRepository.selectPostListByTeam(searchText, teamId, pageable);

        List<PostDto.ResponsePostDto> content = result.getContent();
        content.forEach(responsePostDto -> responsePostDto.setCreateDate(formatTimeAgo(responsePostDto.getCreateTime())));

        return result;
    }

    public PostDto.ResponsePostDto selectPost(Long postId) {
        Post post = postRepository.findByPostId(postId);

        if (post == null) {
            return null;
        }

        post.updateViewCnt();

        return convertToDto(post);
    }

    public boolean savePostLike(String memberId, Long postId) {
        Member member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        Post post = postRepository.findByPostId(postId);
        if (post == null) {
            throw new ApiException(ErrorCode.POST_IS_NOT_FOUND);
        }

        PostLike postlike = postLikeRepository.findByMemberAndPost(member, post);

        if (postlike != null) {
            throw new ApiException(ErrorCode.CAN_NOT_POST_LIKE);
        }

        if (post.getAuthor().getMemberId().equals(memberId)) {
            throw new ApiException(ErrorCode.CAN_NOT_POST_LIKE);
        }

        PostLike postLike = PostLike.builder()
                .member(member)
                .post(post)
                .build();
        postLikeRepository.save(postLike);

        return true;
    }

    private PostDto.ResponsePostDto convertToDto(Post post) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Post, PostDto.ResponsePostDto>() {
            @Override
            protected void configure() {
                skip(destination.getComments());
                skip(destination.getFiles());
                map().setTeamName(source.getFollowedTeam().getTeamName());
            }
        });
        PostDto.ResponsePostDto responsePostDto = modelMapper.map(post, PostDto.ResponsePostDto.class);
        responsePostDto.setAuthorNickname(post.getAuthor().getNickname());
        responsePostDto.setTeamName(post.getFollowedTeam().getTeamName());
        responsePostDto.setSymbol(post.getFollowedTeam().getSymbol());
        responsePostDto.setAuthorId(post.getAuthor().getMemberId());
        responsePostDto.setCreateDate(formatTimeAgo(post.getCreatedDate()));
        responsePostDto.setLikeCnt(post.getPostLikes().size());

        // 댓글
        List<Comment> comments = commentRepository.findCommentsByPostId(post.getPostId());

        List<CommentDto.ResponseCommentDto> commentDtoList = new ArrayList<>();
        Map<Long, CommentDto.ResponseCommentDto> map = new HashMap<>(); //상위 부모를 한번에 알기 위해서 임시로 사용하는 변수

        for (Comment comment : comments) {
            CommentDto.ResponseCommentDto commentDto = modelMapper.map(comment, CommentDto.ResponseCommentDto.class);
            commentDto.setAuthorNickname(comment.getAuthor().getNickname());
            commentDto.setCreateDate(formatTimeAgo(comment.getCreatedDate()));
            commentDto.setAuthorId(comment.getAuthor().getMemberId());
            commentDto.setAuthorTeamName(comment.getAuthor().getFollowedTeam() == null ?
                    "미정" : comment.getAuthor().getFollowedTeam().getTeamName());
            map.put(commentDto.getCommentId(), commentDto);
            if (comment.getParent() != null) {
                map.get(comment.getParent().getCommentId()).getChildren().add(commentDto);
            } else {
                commentDtoList.add(commentDto);
            }
        }

        responsePostDto.setCommentCnt(comments.size());
        responsePostDto.setComments(commentDtoList);
        // 댓글

        List<AttachmentFileDto.SelectAttachmentFileDto> files = attachmentFileService.selectAttachmentFileByPostId(post.getPostId());
        responsePostDto.setFiles(files);

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
