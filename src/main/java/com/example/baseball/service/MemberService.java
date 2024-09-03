package com.example.baseball.service;

import com.example.baseball.domain.Member;
import com.example.baseball.domain.Team;
import com.example.baseball.dto.MemberDto;
import com.example.baseball.repository.MemberRepository;
import com.example.baseball.repository.TeamRepository;
import com.example.baseball.response.exception.ApiException;
import com.example.baseball.response.error.ErrorCode;
import com.example.baseball.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
                .id(dto.getId())
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .followedTeam(findTeam)
                .build();

        memberRepository.save(member);

        return convertToDto(member);
    }

    public MemberDto.ResponseMemberDto updateMember(MemberDto.UpdateMemberRequestDto dto) {
        Member findMember = memberRepository.findByMemberId(dto.getMemberId());
        if (findMember == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        findMember.updateNickname(dto.getNickname());

        return convertToDto(findMember);
    }

    public MemberDto.ResponseMemberDto updatePassword(MemberDto.UpdatePasswordRequestDto dto) {
        Member findMember = memberRepository.findByMemberId(dto.getMemberId());
        if (findMember == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        if (!bCryptPasswordEncoder.matches(dto.getOldPassword(), findMember.getPassword())) {
            throw new ApiException(ErrorCode.PASSWORD_IS_NOT_MATCHED);
        }

        findMember.updatePassword(bCryptPasswordEncoder.encode(dto.getNewPassword()));

        return convertToDto(findMember);
    }

    public void deleteMember(String memberId) {
        Member findMember = memberRepository.findByMemberId(memberId);
        if (findMember == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        findMember.deleteMember();
    }

    public String login(MemberDto.LoginMemberRequestDto dto) {
        Member findMember = memberRepository.findMemberById(dto.getId());
        if (findMember == null || !bCryptPasswordEncoder.matches(dto.getPassword(), findMember.getPassword())) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        findMember.updateLastLoginDate();

        MemberDto.ResponseMemberDto result = convertToDto(findMember);

        return jwtUtil.createAccessToken(result);
    }

    public MemberDto.ResponseMemberDto selectMemberByMemberId(String memberId) {
        Member findMember = memberRepository.findByMemberId(memberId);
        if (findMember == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        return convertToDto(findMember);
    }

    public boolean checkId(String id) {
        Member findMember = memberRepository.findMemberById(id);
        if (findMember != null) {
            throw new ApiException(ErrorCode.ID_IS_DUPLICATE);
        }
        return true;
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

    private MemberDto.ResponseMemberDto convertToDto(Member findMember) {
        MemberDto.ResponseMemberDto result = modelMapper.map(findMember, MemberDto.ResponseMemberDto.class);
        result.setTeamName(findMember.getFollowedTeam() == null ? null : findMember.getFollowedTeam().getTeamName());
        result.setCreatedDate(findMember.getCreatedDate() == null ? null : findMember.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        result.setLastLoginDate(findMember.getCreatedDate() == null ? null : findMember.getLastLoginDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return result;
    }
}