package com.example.baseball.web;

import com.example.baseball.dto.PostDto;
import com.example.baseball.response.error.ErrorCode;
import com.example.baseball.response.model.SingleResult;
import com.example.baseball.response.service.ResponseService;
import com.example.baseball.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ResponseService responseService;

    @PostMapping("/post")
    public SingleResult<?> post(@RequestBody @Valid PostDto.SavePostRequestDto dto, BindingResult bindResult) {
        if (bindResult.hasErrors()) {
            for (ObjectError allError : bindResult.getAllErrors()) {
                return responseService.getFailParameter(allError.getDefaultMessage());
            }
        }

        return responseService.getSingleResult(postService.savePost(dto));
    }

    @PostMapping("/post/{postId}")
    public SingleResult<?> post(@PathVariable("postId") Long postId, @RequestBody PostDto.UpdatePostRequestDto dto) {
        if (postId == null || postId <= 0) {
            return responseService.getFailResult(ErrorCode.POST_IS_NOT_FOUND);
        }
        dto.setPostId(postId);
        if (!dto.getIsUse()) {
            postService.deletePost(dto);
            return responseService.getSuccessResult();
        }

        return responseService.getSingleResult(postService.updatePost(dto));
    }

}
