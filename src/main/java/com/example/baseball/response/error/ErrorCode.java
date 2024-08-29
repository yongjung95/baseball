package com.example.baseball.response.error;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    PARAMETER_IS_EMPTY(400,"C000","파라미터가 올바르지 않습니다."),

    // 회원 관련
    MEMBER_IS_NOT_FOUND(403, "M001", "회원 정보가 없습니다."),
    EMAIL_IS_DUPLICATE(403, "M002", "이미 등록된 이메일입니다."),
    NICKNAME_IS_DUPLICATE(403, "M003", "이미 등록된 닉네임입니다.");


    private final String code;
    private final String message;
    private final int status;

    ErrorCode(int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}
