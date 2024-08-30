package com.example.baseball.repository;

import com.example.baseball.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PostDetailRepository {

    Page<Post> selectPostListByTeam(String searchText, String teamId, Pageable pageable);
    Post findByPostId(Long postId);
}
