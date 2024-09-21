package com.example.baseball.web;

import com.example.baseball.dto.PostDto;
import com.example.baseball.response.error.ErrorCode;
import com.example.baseball.response.model.SingleResult;
import com.example.baseball.response.service.ResponseService;
import com.example.baseball.service.PostService;
import com.example.baseball.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ResponseService responseService;
    private final JwtUtil jwtUtil;

    @PostMapping("/post")
    public SingleResult<?> post(@CookieValue(value = "token") String token, @Valid PostDto.SavePostRequestDto dto, BindingResult bindResult) throws IOException {
        if (bindResult.hasErrors()) {
            for (ObjectError allError : bindResult.getAllErrors()) {
                return responseService.getFailParameter(allError.getDefaultMessage());
            }
        }

        String memberId = jwtUtil.getMemberId(token);
        dto.setAuthorId(memberId);

        return responseService.getSingleResult(postService.savePost(dto));
    }

    @PostMapping("/post/{postId}")
    public SingleResult<?> post(@CookieValue(value = "token") String token,
                                @PathVariable("postId") Long postId, PostDto.UpdatePostRequestDto dto) throws IOException {
        if (postId == null || postId <= 0) {
            return responseService.getFailResult(ErrorCode.POST_IS_NOT_FOUND);
        }
        String memberId = jwtUtil.getMemberId(token);
        dto.setAuthorId(memberId);
        dto.setPostId(postId);

        if (!dto.getIsUse()) {
            postService.deletePost(dto);
            return responseService.getSuccessResult();
        }

        return responseService.getSingleResult(postService.updatePost(dto));
    }

    @DeleteMapping("/post/{postId}")
    public SingleResult<?> deletePost(@CookieValue(value = "token") String token,
                                      @PathVariable("postId") Long postId) {
        if (postId == null || postId <= 0) {
            return responseService.getFailResult(ErrorCode.POST_IS_NOT_FOUND);
        }
        String memberId = jwtUtil.getMemberId(token);

        PostDto.UpdatePostRequestDto dto = new PostDto.UpdatePostRequestDto();
        dto.setAuthorId(memberId);
        dto.setPostId(postId);
        dto.setIsUse(false);

        postService.deletePost(dto);
        return responseService.getSuccessResult();
    }

    @PostMapping("/post/{postId}/like")
    public SingleResult<?> post(@CookieValue(value = "token") String token,
                                @PathVariable("postId") Long postId) {
        if (postId == null || postId <= 0) {
            return responseService.getFailResult(ErrorCode.POST_IS_NOT_FOUND);
        }

        String memberId = jwtUtil.getMemberId(token);

        if (postService.savePostLike(memberId, postId)) {
            return responseService.getSuccessResult();
        } else {
            return responseService.getFailResult(ErrorCode.CAN_NOT_POST_LIKE);
        }
    }
}
