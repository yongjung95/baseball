package com.example.baseball.web;

import com.example.baseball.dto.MemberDto;
import com.example.baseball.response.error.ErrorCode;
import com.example.baseball.response.model.SingleResult;
import com.example.baseball.response.service.ResponseService;
import com.example.baseball.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ResponseService responseService;

    @PostMapping("/member")
    public SingleResult<?> member(@RequestBody @Valid MemberDto.SaveMemberRequestDto dto, BindingResult bindResult) {
        if (bindResult.hasErrors()) {
            for (ObjectError allError : bindResult.getAllErrors()) {
                return responseService.getFailParameter(allError.getDefaultMessage());
            }
        }
        return responseService.getSingleResult(memberService.saveMember(dto));
    }

    @PostMapping("/login")
    public SingleResult<?> login(@RequestBody @Valid MemberDto.LoginMemberRequestDto dto, BindingResult bindResult) {
        if (bindResult.hasErrors()) {
            for (ObjectError allError : bindResult.getAllErrors()) {
                return responseService.getFailParameter(allError.getDefaultMessage());
            }
        }
        return responseService.getSingleResult(memberService.login(dto));
    }

    @PostMapping("/check-email")
    public SingleResult<?> checkEmail(@RequestBody MemberDto.CheckEmailRequestDto dto) {
        String email = dto.getEmail();
        if (!StringUtils.hasText(email)) {
            return responseService.getFailResult(ErrorCode.PARAMETER_IS_EMPTY);
        }
        return responseService.getSingleResult(memberService.checkEmail(email));
    }

    @PostMapping("/check-nickname")
    public SingleResult<?> checkNickname(@RequestBody MemberDto.CheckNicknameRequestDto dto) {
        String nickname = dto.getNickname();
        if (!StringUtils.hasText(nickname)) {
            return responseService.getFailResult(ErrorCode.PARAMETER_IS_EMPTY);
        }
        return responseService.getSingleResult(memberService.checkNickname(nickname));
    }

}
