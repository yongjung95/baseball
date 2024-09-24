package com.example.baseball.service;


import com.example.baseball.domain.Comment;
import com.example.baseball.domain.Member;
import com.example.baseball.domain.Post;
import com.example.baseball.dto.CommentDto;
import com.example.baseball.repository.CommentRepository;
import com.example.baseball.repository.MemberRepository;
import com.example.baseball.repository.PostRepository;
import com.example.baseball.response.error.ErrorCode;
import com.example.baseball.response.exception.ApiException;
import com.example.baseball.util.FormatUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public CommentDto.SendTopicCommentDto saveComment(CommentDto.SaveCommentDto dto) {
        Post post = postRepository.findByPostId(dto.getPostId());
        if (post == null) {
            throw new ApiException(ErrorCode.POST_IS_NOT_FOUND);
        }

        Member member = memberRepository.findByMemberId(dto.getAuthorId());
        if (member == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        Comment parent = null;
        if (dto.getParentId() != null) {
            parent = commentRepository.findByCommentId(dto.getParentId());
        }

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .post(post)
                .parent(parent)
                .author(member)
                .build();

        commentRepository.save(comment);

        CommentDto.SendTopicCommentDto result = new CommentDto.SendTopicCommentDto();
        result.setPostAuthorId(comment.getParent() == null ? post.getAuthor().getMemberId() : comment.getParent().getAuthor().getMemberId());
        result.setPostId(post.getPostId());

        return result;
    }

    public void deleteComment(CommentDto.DeleteCommentDto dto) {
        Member member = memberRepository.findByMemberId(dto.getAuthorId());
        if (member == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        Comment findComment = commentRepository.findByCommentId(dto.getCommentId());
        if (findComment == null) {
            throw new ApiException(ErrorCode.COMMENT_IS_NOT_MATCHED);
        }

        if (!findComment.getAuthor().getMemberId().equals(dto.getAuthorId())) {
            throw new ApiException(ErrorCode.COMMENT_AUTHOR_IS_NOT_MATCHED);
        }

        findComment.changeIsUse();
    }

    public List<CommentDto.ResponseCommentDto> selectCommentList(Long postId) {
        List<Comment> comments = commentRepository.findCommentsByPostId(postId);

        List<CommentDto.ResponseCommentDto> commentDtoList = new ArrayList<>();
        Map<Long, CommentDto.ResponseCommentDto> map = new HashMap<>(); //상위 부모를 한번에 알기 위해서 임시로 사용하는 변수

        for (Comment comment : comments) {
            CommentDto.ResponseCommentDto commentDto = CommentDto.ResponseCommentDto.builder()
                    .commentId(comment.getCommentId())
                    .content(comment.getContent())
                    .postId(comment.getPost().getPostId())
                    .authorNickname(comment.getAuthor().getNickname())
                    .authorTeamName(comment.getAuthor().getFollowedTeam() == null ?
                            "미정" : comment.getAuthor().getFollowedTeam().getTeamName())
                    .authorId(comment.getAuthor().getMemberId())
                    .createDate(FormatUtil.formatTimeAgo(comment.getCreatedDate()))
                    .isUse(comment.getIsUse())
                    .build();

            map.put(commentDto.getCommentId(), commentDto);
            if (comment.getParent() != null) {
                map.get(comment.getParent().getCommentId()).getChildren().add(commentDto);
            } else {
                commentDtoList.add(commentDto);
            }
        }

        return commentDtoList;
    }

}
