package com.example.baseball.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class MemberDto {

    @Data
    public static class SaveMemberRequestDto {
        @NotBlank(message = "이메일을 입력해주세요.")
        private String email;
        @NotBlank(message = "패스워드를 입력해주세요.")
        private String password;
        @NotBlank(message = "닉네임을 입력해주세요.")
        private String nickname;
        private String teamId;
    }

    @Data
    public static class ResponseMemberDto {
        private String memberId;
        private String email;
        private String nickname;
        private String teamName;
    }

    @Data
    public static class LoginMemberRequestDto {
        @NotBlank(message = "이메일을 입력해주세요.")
        private String email;
        @NotBlank(message = "패스워드를 입력해주세요.")
        private String password;
    }

    @Data
    public static class CheckEmailRequestDto {
        private String email;
    }

    @Data
    public static class CheckNicknameRequestDto {
        private String nickname;
    }
}
