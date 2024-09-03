package com.example.baseball.service;


import com.example.baseball.domain.Comment;
import com.example.baseball.domain.Member;
import com.example.baseball.domain.Post;
import com.example.baseball.dto.CommentDto;
import com.example.baseball.repository.CommentRepository;
import com.example.baseball.repository.MemberRepository;
import com.example.baseball.repository.PostRepository;
import com.example.baseball.response.exception.ApiException;
import com.example.baseball.response.error.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

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

}
