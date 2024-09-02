package com.example.baseball.repository;

import com.example.baseball.domain.Member;
import com.example.baseball.domain.Post;
import com.example.baseball.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    PostLike findByMemberAndPost(Member member, Post post);
}
