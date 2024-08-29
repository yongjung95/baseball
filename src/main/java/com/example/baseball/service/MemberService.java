package com.example.baseball.service;

import com.example.baseball.domain.Member;
import com.example.baseball.domain.Team;
import com.example.baseball.dto.MemberDto;
import com.example.baseball.repository.MemberRepository;
import com.example.baseball.repository.TeamRepository;
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
                .passwd(dto.getPassword())
                .follwedTeam(findTeam)
                .build();

        memberRepository.save(member);

        MemberDto.ResponseMemberDto result = modelMapper.map(member, MemberDto.ResponseMemberDto.class);
        result.setTeamName(member.getFollwedTeam() == null ? null : member.getFollwedTeam().getTeamName());

        return result;
    }
}