package com.example.baseball.dto;

import lombok.Data;

public class ChatDto {

    @Data
    public static class SendChatDto {
        private String chatAuthorId;
        private String content;
    }

    @Data
    public static class ReceiveChatDto {
        private String chatAuthorId;
        private String chatAuthorNickname;
        private String content;
        private String teamName;
        private String teamLogo;
    }

}
