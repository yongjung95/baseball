package com.example.baseball.response.error;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private ErrorCode error;

    public ApiException(ErrorCode e) {
        super(e.getMessage());
        this.error = e;
    }
}
