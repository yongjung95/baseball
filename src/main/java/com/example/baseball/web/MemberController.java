package com.example.baseball.web;

import com.example.baseball.dto.MemberDto;
import com.example.baseball.response.model.SingleResult;
import com.example.baseball.response.service.ResponseService;
import com.example.baseball.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ResponseService responseService;

    @PostMapping("/member")
    public SingleResult<?> member(@RequestBody MemberDto.SaveMemberRequestDto dto) {
        return responseService.getSingleResult(memberService.saveMember(dto));
    }
}
