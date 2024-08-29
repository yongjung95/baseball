package com.example.baseball.service;

import com.example.baseball.dto.TeamDto;
import com.example.baseball.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    public TeamDto.SelectTeamDto selectTeam(String teamId) {
        return modelMapper.map(teamRepository.findByTeamId(teamId), TeamDto.SelectTeamDto.class);
    }

    public List<TeamDto.SelectTeamDto> selectTeamDtoList() {
        return teamRepository.findAll().stream().map(team -> modelMapper.map(team, TeamDto.SelectTeamDto.class)).collect(Collectors.toList());
    }
}
