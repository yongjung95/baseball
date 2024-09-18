package com.example.baseball.service;

import com.example.baseball.domain.Team;
import com.example.baseball.dto.NewsDto;
import com.example.baseball.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.unbescape.html.HtmlEscape;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final TeamRepository teamRepository;

    @Cacheable("newsList")
    public List<NewsDto.SelectNewsDto> dateNews(String teamId) {
        Team findTeam = teamRepository.findByTeamId(teamId);

        List<NewsDto.SelectNewsDto> newsList = new ArrayList<>();
        JSONArray jsonArray = getNewsList(findTeam.getTeamName(), 1);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss x").withLocale(Locale.ENGLISH);

            LocalDateTime localDateTime = LocalDateTime.parse(object.get("pubDate").toString(), formatter);

            formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일(E) HH:mm");

            String title = HtmlEscape.unescapeHtml(NewsDto.SelectNewsDto.replace(object.get("title").toString()));

            if (title.contains(findTeam.getSymbol()) || title.contains(findTeam.getShortName())
                    || title.contains(findTeam.getTeamName())) {
                NewsDto.SelectNewsDto news = NewsDto.SelectNewsDto.builder()
                        .teamId(findTeam.getTeamId())
                        .teamName(findTeam.getTeamName())
                        .title(title)
                        .link(object.get("link").toString())
                        .description(HtmlEscape.unescapeHtml(NewsDto.SelectNewsDto.replace(object.get("description").toString())))
                        .pubDate(localDateTime.format(formatter))
                        .build();

                newsList.add(news);
            }

            if (newsList.size() >= 10) {
                break;
            }
        }

        if (newsList.size() < 10) {
            jsonArray = getNewsList(findTeam.getTeamName(), 100);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss x").withLocale(Locale.ENGLISH);

                LocalDateTime localDateTime = LocalDateTime.parse(object.get("pubDate").toString(), formatter);

                formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일(E) HH:mm");

                String title = HtmlEscape.unescapeHtml(NewsDto.SelectNewsDto.replace(object.get("title").toString()));

                if (title.contains(findTeam.getSymbol()) || title.contains(findTeam.getShortName())
                        || title.contains(findTeam.getTeamName())) {
                    NewsDto.SelectNewsDto news = NewsDto.SelectNewsDto.builder()
                            .teamId(findTeam.getTeamId())
                            .teamName(findTeam.getTeamName())
                            .title(title)
                            .link(object.get("link").toString())
                            .description(HtmlEscape.unescapeHtml(NewsDto.SelectNewsDto.replace(object.get("description").toString())))
                            .pubDate(localDateTime.format(formatter))
                            .build();

                    newsList.add(news);
                }

                if (newsList.size() >= 10) {
                    break;
                }
            }
        }

        return newsList;
    }

    private JSONArray getNewsList(String teamName, int cnt) {
        String apiURL = "https://openapi.naver.com/v1/search/news.json?sort=sim&display=100&query=" + teamName + "&start=" + cnt;    // json 결과

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders header = new HttpHeaders();
        header.set("X-Naver-Client-Id", "Cp_LNffxS_tlbT3cX7aG");
        header.set("X-Naver-Client-Secret", "x6kNCg2l3r");

        HttpEntity<?> entity = new HttpEntity<>(header);

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(apiURL).build();

        ResponseEntity<String> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);

        JSONObject jsonObject = new JSONObject(resultMap.getBody());

        return new JSONArray(jsonObject.getJSONArray("items").toString());
    }
}
