package com.example.baseball.web;

import com.example.baseball.dto.CommentDto;
import com.example.baseball.response.error.ErrorCode;
import com.example.baseball.response.model.SingleResult;
import com.example.baseball.response.service.ResponseService;
import com.example.baseball.service.CommentService;
import com.example.baseball.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final ResponseService responseService;
    private final JwtUtil jwtUtil;
    private final SimpMessagingTemplate messagingTemplate;

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

    // STOMP를 통한 댓글 알림 전송
    @MessageMapping("/comment")
    public void sendComment(@RequestBody @Valid CommentDto.SendTopicCommentDto dto) {
        // 댓글을 데이터베이스에 저장하는 로직 (여기서는 중복될 수 있음)

        // 클라이언트에 알림 전송
        messagingTemplate.convertAndSend("/topic/comments/" + dto.getPostAuthorId(), dto);
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
