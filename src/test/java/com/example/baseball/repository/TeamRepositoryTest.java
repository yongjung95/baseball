package com.example.baseball.repository;

import com.example.baseball.domain.Team;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 팀명으로_조회() throws Exception {
        //given
        Team team = Team.builder()
                .teamId(UUID.randomUUID().toString())
                .teamName("SSG 랜더스")
                .build();

        teamRepository.save(team);

        entityManager.flush();
        entityManager.clear();

        //when
        Team findTeam = teamRepository.findByTeamName("SSG 랜더스");

        //then
        assertThat(findTeam.getTeamName()).isEqualTo(team.getTeamName());
    }
}