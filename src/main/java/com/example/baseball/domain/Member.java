package com.example.baseball.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    private String memberId;
    private String id;
    private String email;
    private String nickname;
    private String password;
    private LocalDateTime lastLoginDate;
    @Builder.Default
    private boolean isUse = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team followedTeam;

    public void updateLastLoginDate() {
        this.lastLoginDate = LocalDateTime.now();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void deleteMember() {
        this.isUse = false;
    }
}
