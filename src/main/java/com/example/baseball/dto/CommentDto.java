package com.example.baseball.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class CommentDto {

    @Data
    public static class ResponseCommentDto {
        private Long commentId;
        private String content;
        private Long postId;
        private Long parentId;
        private String authorNickname;
        private String authorTeamName;
        private String authorId;
        private String createDate;
        private boolean isUse;
        private List<ResponseCommentDto> children = new ArrayList<>();
    }

    @Data
    public static class SaveCommentDto {
        private String authorId;
        @NotNull(message = "게시글을 입력해주세요.")
        private Long postId;
        private Long parentId;
        @NotBlank(message = "내용을 입력해주세요.")
        private String content;
    }

    @Data
    public static class DeleteCommentDto {
        private String authorId;
        private Long commentId;
    }

}
