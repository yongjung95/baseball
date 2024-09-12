package com.example.baseball.repository;

import com.example.baseball.domain.Post;
import com.example.baseball.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PostDetailRepository {

    Page<PostDto.ResponsePostDto> selectPostListByTeam(String searchText, String teamId, Pageable pageable);
    Post findByPostId(Long postId);
}
