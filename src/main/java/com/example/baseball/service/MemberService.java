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

        Member member = Member.builder()
                .memberId(UUID.randomUUID().toString())
                .email(dto.getEmail())
                .nickName(dto.getNickName())
                .passwd(dto.getPasswd())
                .follwedTeam(findTeam)
                .build();

        memberRepository.save(member);

        MemberDto.ResponseMemberDto result = modelMapper.map(member, MemberDto.ResponseMemberDto.class);
        result.setTeamName(member.getFollwedTeam() == null ? null : member.getFollwedTeam().getTeamName());

        return result;
    }

    public MemberDto.ResponseMemberDto login(MemberDto.LoginMemberRequestDto dto) {
        Member findMember = memberRepository.findByEmail(dto.getEmail());
        if (findMember == null || !findMember.getPasswd().equals(dto.getPasswd())) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        MemberDto.ResponseMemberDto result = modelMapper.map(findMember, MemberDto.ResponseMemberDto.class);
        result.setTeamName(findMember.getFollwedTeam() == null ? null : findMember.getFollwedTeam().getTeamName());

        return result;
    }
}