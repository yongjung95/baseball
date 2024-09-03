package com.example.baseball.web;

import com.example.baseball.dto.CommentDto;
import com.example.baseball.response.error.ErrorCode;
import com.example.baseball.response.model.SingleResult;
import com.example.baseball.response.service.ResponseService;
import com.example.baseball.service.CommentService;
import com.example.baseball.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final ResponseService responseService;
    private final JwtUtil jwtUtil;

    @PostMapping("/comment")
    public SingleResult<?> comment(@CookieValue(value = "token") String token, @RequestBody @Valid CommentDto.SaveCommentDto dto, BindingResult bindResult) {
        if (bindResult.hasErrors()) {
            for (ObjectError allError : bindResult.getAllErrors()) {
                return responseService.getFailParameter(allError.getDefaultMessage());
            }
        }

        String memberId = jwtUtil.getMemberId(token);
        dto.setAuthorId(memberId);

        return responseService.getSingleResult(commentService.saveComment(dto));
    }

    @PostMapping("/comment/{commentId}")
    public SingleResult<?> comment(@CookieValue(value = "token") String token, @PathVariable(value = "commentId") Long commentId) {
        if (commentId == null) {
            return responseService.getFailResult(ErrorCode.PARAMETER_IS_EMPTY);
        }

        CommentDto.DeleteCommentDto dto = new CommentDto.DeleteCommentDto();
        dto.setCommentId(commentId);
        dto.setAuthorId(jwtUtil.getMemberId(token));

        commentService.deleteComment(dto);
        return responseService.getSuccessResult();
    }
}
