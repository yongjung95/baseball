package com.example.baseball.dto;

import lombok.Data;

public class MemberDto {

    @Data
    public static class SaveMemberRequestDto {
        private String email;
        private String password;
        private String nickName;
        private String teamId;
    }

    @Data
    public static class ResponseMemberDto {
        private String memberId;
        private String email;
        private String nickName;
        private String teamName;
    }
}
