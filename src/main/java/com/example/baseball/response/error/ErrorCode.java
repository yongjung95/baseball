package com.example.baseball.response.error;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    PARAMETER_IS_EMPTY(400,"C000","파라미터가 올바르지 않습니다."),

    // 회원 관련
    MEMBER_IS_NOT_FOUND(403, "M001", "회원 정보가 없습니다."),
    EMAIL_IS_DUPLICATE(403, "M002", "이미 사용중인 이메일입니다."),
    NICKNAME_IS_DUPLICATE(403, "M003", "이미 사용중인 닉네임입니다."),
    PASSWORD_IS_NOT_MATCHED(400, "M004", "기존 패스워드가 일치 하지 않습니다."),
    ID_IS_DUPLICATE(403, "M005", "이미 사용중인 아이디입니다."),

    // 팀 관련
    TEAM_IS_NOT_FOUND(403, "T001", "팀 정보가 없습니다."),

    // 게시글 관련
    POST_IS_NOT_FOUND(403, "P001", "게시글 정보가 없습니다."),
    AUTHOR_IS_NOT_MATCHED(400, "P002", "작성자와 일치 하지 않습니다"),
    POST_CREATE_IS_ONLY_SELECTED_TEAM(400, "P003", "게시글은 팀을 선택했을 때만 작성 가능합니다."),
    CAN_NOT_POST_LIKE(400, "P004", "추천을 할 수 없습니다."),

    // 댓글 관련
    COMMENT_IS_NOT_MATCHED(400, "CO001", "댓글 정보가 없습니다."),
    COMMENT_AUTHOR_IS_NOT_MATCHED(400, "CO001", "댓글 작성자와 일치 하지 않습니다.");


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
