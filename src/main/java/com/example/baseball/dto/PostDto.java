package com.example.baseball.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class PostDto {

    @Data
    public static class SavePostRequestDto {
        @NotBlank(message = "제목을 입력해주세요.")
        private String title;
        @NotBlank(message = "내용을 입력해주세요.")
        private String content;
        private String authorId;
    }

    @Data
    public static class UpdatePostRequestDto {
        private Long postId;
        private String title;
        private String content;
        private String authorId;
        private Boolean isUse = true;
    }

    @Data
    public static class ResponsePostDto {
        private String postId;
        private String title;
        private String content;
        private String authorId;
        private String authorNickname;
        private String teamName;
        private String symbol;
        private String createDate;
        private Integer likeCnt;
        private Integer viewCnt;
        private Integer commentCnt;
        private List<CommentDto.ResponseCommentDto> comments;
    }

    @Data
    public static class SelectPostRequestDto {
        private String searchText = "";
        private int page = 0;
        private int size = 10;
    }
}
