package com.example.baseball.repository;

import com.example.baseball.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, String> {

    Team findByTeamName(String teamName);
    Team findByTeamId(String teamId);
}
