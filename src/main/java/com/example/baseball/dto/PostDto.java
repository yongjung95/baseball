package com.example.baseball.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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
        private String authorNickname;
        private String teamName;
    }
}
