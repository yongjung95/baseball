package com.example.baseball.service;

import com.example.baseball.domain.Member;
import com.example.baseball.domain.Team;
import com.example.baseball.dto.MemberDto;
import com.example.baseball.repository.MemberRepository;
import com.example.baseball.repository.TeamRepository;
import com.example.baseball.response.error.ApiException;
import com.example.baseball.response.error.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    public MemberDto.ResponseMemberDto saveMember(MemberDto.SaveMemberRequestDto dto) {
        Team findTeam = null;
        if (StringUtils.hasText(dto.getTeamId())) {
             findTeam = teamRepository.findByTeamId(dto.getTeamId());
        }

        Member findMember = memberRepository.findByEmail(dto.getEmail());
        if (findMember != null) {
            throw new ApiException(ErrorCode.EMAIL_IS_DUPLICATE);
        }

        Member member = Member.builder()
                .memberId(UUID.randomUUID().toString())
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .followedTeam(findTeam)
                .build();

        memberRepository.save(member);

        MemberDto.ResponseMemberDto result = modelMapper.map(member, MemberDto.ResponseMemberDto.class);
        result.setTeamName(member.getFollowedTeam() == null ? null : member.getFollowedTeam().getTeamName());

        return result;
    }

    public MemberDto.ResponseMemberDto login(MemberDto.LoginMemberRequestDto dto) {
        Member findMember = memberRepository.findByEmail(dto.getEmail());
        if (findMember == null || !findMember.getPassword().equals(dto.getPassword())) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        MemberDto.ResponseMemberDto result = modelMapper.map(findMember, MemberDto.ResponseMemberDto.class);
        result.setTeamName(findMember.getFollowedTeam() == null ? null : findMember.getFollowedTeam().getTeamName());

        return result;
    }

    public boolean checkEmail(String email) {
        Member findMember = memberRepository.findByEmail(email);
        if (findMember != null) {
            throw new ApiException(ErrorCode.EMAIL_IS_DUPLICATE);
        }
        return true;
    }

    public boolean checkNickname(String nickname) {
        Member findMember = memberRepository.findByNickname(nickname);
        if (findMember != null) {
            throw new ApiException(ErrorCode.NICKNAME_IS_DUPLICATE);
        }
        return true;
    }
}