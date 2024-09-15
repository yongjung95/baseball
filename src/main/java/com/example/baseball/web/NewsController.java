package com.example.baseball.web;

import com.example.baseball.dto.TeamDto;
import com.example.baseball.response.model.ListResult;
import com.example.baseball.response.service.ResponseService;
import com.example.baseball.service.NewsService;
import com.example.baseball.service.TeamService;
import com.example.baseball.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final TeamService teamService;
    private final ResponseService responseService;
    private final JwtUtil jwtUtil;

    @GetMapping("/news-list")
    public ListResult<?> index(@CookieValue(value = "token", required = false) String token, @RequestParam("teamId") String teamId) {
        // 로그인 여부
        String teamName = null;

        if (!StringUtils.hasText(teamId) && !jwtUtil.validateToken(token)) {
            teamName = "LG 트윈스";
        } else if (!StringUtils.hasText(teamId) && jwtUtil.validateToken(token)) {
            teamName = jwtUtil.getTeamName(token);
        } else {
            TeamDto.SelectTeamDto selectTeamDto = teamService.selectTeam(teamId);

            return responseService.getListResult(newsService.dateNews(selectTeamDto.getTeamId()));
        }

        TeamDto.SelectTeamDto selectTeamDto = teamService.selectTeamByTeamName(teamName);

        return responseService.getListResult(newsService.dateNews(selectTeamDto.getTeamId()));
    }

}
