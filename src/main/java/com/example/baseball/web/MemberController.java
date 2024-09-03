package com.example.baseball.web;

import com.example.baseball.dto.MemberDto;
import com.example.baseball.response.error.ErrorCode;
import com.example.baseball.response.model.SingleResult;
import com.example.baseball.response.service.ResponseService;
import com.example.baseball.service.MemberService;
import com.example.baseball.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ResponseService responseService;
    private final JwtUtil jwtUtil;

    @PostMapping("/member")
    public SingleResult<?> member(@RequestBody @Valid MemberDto.SaveMemberRequestDto dto, BindingResult bindResult) {
        if (bindResult.hasErrors()) {
            for (ObjectError allError : bindResult.getAllErrors()) {
                return responseService.getFailParameter(allError.getDefaultMessage());
            }
        }
        return responseService.getSingleResult(memberService.saveMember(dto));
    }

    @PostMapping("/member/edit")
    public SingleResult<?> memberEdit(@CookieValue(value = "token") String token,
                                      @RequestBody @Valid MemberDto.UpdateMemberRequestDto dto,
                                      BindingResult bindResult) {
        if (bindResult.hasErrors()) {
            for (ObjectError allError : bindResult.getAllErrors()) {
                return responseService.getFailParameter(allError.getDefaultMessage());
            }
        }

        String memberId = jwtUtil.getMemberId(token);
        dto.setMemberId(memberId);

        return responseService.getSingleResult(memberService.updateMember(dto));
    }

    @DeleteMapping("/member")
    public SingleResult<?> member(@CookieValue(value = "token") String token, HttpServletResponse response) throws IOException {
        String memberId = jwtUtil.getMemberId(token);
        memberService.deleteMember(memberId);

        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return responseService.getSuccessResult();
    }

    @PostMapping("/member/password")
    public SingleResult<?> memberPassword(@CookieValue(value = "token") String token,
                                          @RequestBody @Valid MemberDto.UpdatePasswordRequestDto dto,
                                          BindingResult bindResult) {
        if (bindResult.hasErrors()) {
            for (ObjectError allError : bindResult.getAllErrors()) {
                return responseService.getFailParameter(allError.getDefaultMessage());
            }
        }

        String memberId = jwtUtil.getMemberId(token);
        dto.setMemberId(memberId);

        return responseService.getSingleResult(memberService.updatePassword(dto));
    }

    @PostMapping("/login")
    public SingleResult<?> login(@RequestBody @Valid MemberDto.LoginMemberRequestDto dto, BindingResult bindResult, HttpServletResponse response) {
        if (bindResult.hasErrors()) {
            for (ObjectError allError : bindResult.getAllErrors()) {
                return responseService.getFailParameter(allError.getDefaultMessage());
            }
        }
        String token = memberService.login(dto);
        response.addHeader("Set-Cookie", "token=" + token + "; HttpOnly; Secure; Path=/; Max-Age=3600;");

        return responseService.getSuccessResult();
    }

    @PostMapping("/check-id")
    public SingleResult<?> checkId(@RequestBody MemberDto.CheckIdRequestDto dto) {
        String id = dto.getId();
        if (!StringUtils.hasText(id)) {
            return responseService.getFailResult(ErrorCode.PARAMETER_IS_EMPTY);
        }
        return responseService.getSingleResult(memberService.checkId(id));
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
