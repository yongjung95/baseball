package com.example.baseball.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AttachmentFileDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectAttachmentFileDto {
        private Long id;
        private String fileName;
        private String fileOriginalName;
        private String filePath;
        private Long fileSize;
        private String fileExtension;
        private String fileContentType;
        private boolean isUse;
        private Long postId;
        private String regMemberId;
    }

}
