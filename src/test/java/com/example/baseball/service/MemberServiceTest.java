package com.example.baseball.service;

import com.example.baseball.domain.Team;
import com.example.baseball.dto.MemberDto;
import com.example.baseball.repository.TeamRepository;
import com.example.baseball.response.error.ApiException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    void 회원_생성() throws Exception {
        //given
        MemberDto.SaveMemberRequestDto requestDto = new MemberDto.SaveMemberRequestDto();
        requestDto.setEmail("yongjung95@gmail.com");
        requestDto.setPasswd("1234");
        requestDto.setNickName("정이");

        //when
        MemberDto.ResponseMemberDto responseMemberDto = memberService.saveMember(requestDto);

        //then
        assertThat(responseMemberDto.getNickName()).isEqualTo(requestDto.getNickName());
    }

    @Test
    void 회원_팀_매칭() throws Exception {
        //given
        Team findTeam = teamRepository.findByTeamName("SSG 랜더스");

        MemberDto.SaveMemberRequestDto requestDto = new MemberDto.SaveMemberRequestDto();
        requestDto.setEmail("yongjung95@gmail.com");
        requestDto.setPasswd("1234");
        requestDto.setNickName("정이");
        requestDto.setTeamId(findTeam.getTeamId());

        //when
        MemberDto.ResponseMemberDto responseMemberDto = memberService.saveMember(requestDto);

        //then
        assertThat(responseMemberDto.getTeamName()).isEqualTo("SSG 랜더스");
    }

    @Test
    void 회원_팀_없이_매칭() throws Exception {
        //given
        MemberDto.SaveMemberRequestDto requestDto = new MemberDto.SaveMemberRequestDto();
        requestDto.setEmail("yongjung95@gmail.com");
        requestDto.setPasswd("1234");
        requestDto.setNickName("정이");
        //when
        MemberDto.ResponseMemberDto responseMemberDto = memberService.saveMember(requestDto);

        //then
        assertThat(responseMemberDto.getTeamName()).isNull();
    }

    @Test
    void 로그인_성공() {
        //given
        MemberDto.SaveMemberRequestDto requestDto = new MemberDto.SaveMemberRequestDto();
        requestDto.setEmail("yongjung95@gmail.com");
        requestDto.setPasswd("1234");
        requestDto.setNickName("정이");

        memberService.saveMember(requestDto);

        //when
        MemberDto.LoginMemberRequestDto dto = new MemberDto.LoginMemberRequestDto();
        dto.setEmail("yongjung95@gmail.com");
        dto.setPasswd("1234");

        MemberDto.ResponseMemberDto result = memberService.login(dto);

        //then

        assertThat(result.getNickName()).isEqualTo("정이");
    }

    @Test
    void 로그인_실패() {
        //given
        MemberDto.SaveMemberRequestDto requestDto = new MemberDto.SaveMemberRequestDto();
        requestDto.setEmail("yongjung95@gmail.com");
        requestDto.setPasswd("1234");
        requestDto.setNickName("정이");

        memberService.saveMember(requestDto);

        //when
        MemberDto.LoginMemberRequestDto dto = new MemberDto.LoginMemberRequestDto();
        dto.setEmail("yongjung95@gmail.com");
        dto.setPasswd("1233");

        //then
        Assertions.assertThatThrownBy(() -> memberService.login(dto)).isInstanceOf(ApiException.class);
    }
}