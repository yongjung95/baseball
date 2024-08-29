package com.example.baseball.dto;

import lombok.Data;

public class TeamDto {

    @Data
    public static class SelectTeamDto {
        private String teamId;
        private String teamName;
        private String logo;
        private String description;
    }
}
