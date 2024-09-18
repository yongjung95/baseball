package com.example.baseball.service;

import com.example.baseball.dto.NewsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class NewsServiceTest {

    @Autowired
    NewsService newsService;

    @Test
    void 뉴스_조회() throws Exception {
        //given
        String teamId = "189381b6-33a4-49e2-9d33-0aa553298e85"; // 랜더스

        //when
        List<NewsDto.SelectNewsDto> result = newsService.dateNews(teamId);

        //then
        Assertions.assertThat(result.size()).isEqualTo(10);
    }
}