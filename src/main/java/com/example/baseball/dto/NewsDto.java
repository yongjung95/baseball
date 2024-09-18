package com.example.baseball.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class NewsDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectNewsDto {
        private String teamName;
        private String teamId;
        private String title;
        private String link;
        private String description;
        private String pubDate;

        public static String replace(String text){
            return text.replace("<b>", "")
                    .replace("</b>", "")
                    .replace("&quot;", "\"");
        }
    }
}
