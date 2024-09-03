package com.example.baseball.repository;

import com.example.baseball.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c join fetch c.author left join fetch c.author.followedTeam WHERE c.post.postId = :postId ORDER BY c.parent.id ASC NULLS FIRST, c.id ASC")
    List<Comment> findCommentsByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c join fetch c.author WHERE c.commentId =:commentId")
    Comment findByCommentId(@Param("commentId") Long commentId);
}
