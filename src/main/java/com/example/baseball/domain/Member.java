package com.example.baseball.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    private String memberId;
    private String email;
    private String nickName;
    private String passwd;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team follwedTeam;
}
