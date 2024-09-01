package com.example.baseball.service;

import com.example.baseball.domain.Team;
import com.example.baseball.dto.MemberDto;
import com.example.baseball.repository.TeamRepository;
import com.example.baseball.response.error.ApiException;
import com.example.baseball.util.JwtUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void 회원_생성() throws Exception {
        //given
        MemberDto.SaveMemberRequestDto requestDto = new MemberDto.SaveMemberRequestDto();
        requestDto.setEmail("yongjung95@gmail.com");
        requestDto.setPassword("1234");
        requestDto.setNickname("정이");

        //when
        MemberDto.ResponseMemberDto responseMemberDto = memberService.saveMember(requestDto);

        //then
        assertThat(responseMemberDto.getNickname()).isEqualTo(requestDto.getNickname());
    }

    @Test
    void 회원_팀_매칭() throws Exception {
        //given
        Team findTeam = teamRepository.findByTeamName("SSG 랜더스");

        MemberDto.SaveMemberRequestDto requestDto = new MemberDto.SaveMemberRequestDto();
        requestDto.setEmail("yongjung95@gmail.com");
        requestDto.setPassword("1234");
        requestDto.setNickname("정이");
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
        requestDto.setPassword("1234");
        requestDto.setNickname("정이");
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
        requestDto.setPassword("1234");
        requestDto.setNickname("정이");

        memberService.saveMember(requestDto);

        //when
        MemberDto.LoginMemberRequestDto dto = new MemberDto.LoginMemberRequestDto();
        dto.setEmail("yongjung95@gmail.com");
        dto.setPassword("1234");


        //then
        String jwtToken = memberService.login(dto);

        assertThat(jwtUtil.getMemberId(jwtToken)).isEqualTo("정이");
    }

    @Test
    void 로그인_실패() {
        //given
        MemberDto.SaveMemberRequestDto requestDto = new MemberDto.SaveMemberRequestDto();
        requestDto.setEmail("yongjung95@gmail.com");
        requestDto.setPassword("1234");
        requestDto.setNickname("정이");

        memberService.saveMember(requestDto);

        //when
        MemberDto.LoginMemberRequestDto dto = new MemberDto.LoginMemberRequestDto();
        dto.setEmail("yongjung95@gmail.com");
        dto.setPassword("1233");

        //then
        Assertions.assertThatThrownBy(() -> memberService.login(dto)).isInstanceOf(ApiException.class);
    }

    @Test
    void 이메일_중복확인() {
        //given
        MemberDto.SaveMemberRequestDto requestDto = new MemberDto.SaveMemberRequestDto();
        requestDto.setEmail("yongjung95@gmail.com");
        requestDto.setPassword("1234");
        requestDto.setNickname("정이");

        memberService.saveMember(requestDto);

        //when
        boolean result = memberService.checkEmail("yongjung9@gmail.com");

        //then
        assertThat(result).isTrue();
    }

    @Test
    void 이메일_중복확인_실패() {
        //given
        MemberDto.SaveMemberRequestDto requestDto = new MemberDto.SaveMemberRequestDto();
        requestDto.setEmail("yongjung95@gmail.com");
        requestDto.setPassword("1234");
        requestDto.setNickname("정이");

        memberService.saveMember(requestDto);

        //when
        String email = "yongjung95@gmail.com";

        //then
        Assertions.assertThatThrownBy(() -> memberService.checkEmail(email)).isInstanceOf(ApiException.class);
    }

    @Test
    void 닉네임_중복확인() {
        //given
        MemberDto.SaveMemberRequestDto requestDto = new MemberDto.SaveMemberRequestDto();
        requestDto.setEmail("yongjung95@gmail.com");
        requestDto.setPassword("1234");
        requestDto.setNickname("정이");

        memberService.saveMember(requestDto);

        //when
        boolean result = memberService.checkNickname("정이2");

        //then
        assertThat(result).isTrue();
    }

    @Test
    void 닉네임_중복확인_실패() {
        //given
        MemberDto.SaveMemberRequestDto requestDto = new MemberDto.SaveMemberRequestDto();
        requestDto.setEmail("yongjung95@gmail.com");
        requestDto.setPassword("1234");
        requestDto.setNickname("정이");

        memberService.saveMember(requestDto);

        //when
        String nickname = "정이";

        //then
        Assertions.assertThatThrownBy(() -> memberService.checkNickname(nickname)).isInstanceOf(ApiException.class);

    }
}