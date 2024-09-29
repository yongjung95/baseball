package com.example.baseball.service;

import com.example.baseball.dto.TeamDto;
import com.example.baseball.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    @Cacheable("team")
    public TeamDto.SelectTeamDto selectTeam(String teamId) {
        return modelMapper.map(teamRepository.findByTeamId(teamId), TeamDto.SelectTeamDto.class);
    }

    @Cacheable("teamList")
    public List<TeamDto.SelectTeamDto> selectTeamDtoList() {
        return teamRepository.findAll().stream().map(team -> modelMapper.map(team, TeamDto.SelectTeamDto.class)).collect(Collectors.toList());
    }

    @Cacheable("teamBySymbol")
    public TeamDto.SelectTeamDto selectTeamBySymbol(String symbol) {
        return modelMapper.map(teamRepository.findBySymbol(symbol), TeamDto.SelectTeamDto.class);
    }

    public TeamDto.SelectTeamDto selectTeamByTeamName(String teamName) {
        return modelMapper.map(teamRepository.findByTeamName(teamName), TeamDto.SelectTeamDto.class);
    }
}
