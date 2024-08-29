package com.example.baseball.response.error;

import com.example.baseball.response.model.SingleResult;
import com.example.baseball.response.service.ResponseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionAdvice {

    private final ResponseService responseService;

    @ExceptionHandler({ApiException.class})
    public SingleResult<?> exceptionHandler(HttpServletRequest request, final ApiException e) {

        ErrorCode error = e.getError();

        return responseService.getFailResult(error);
    }
}
