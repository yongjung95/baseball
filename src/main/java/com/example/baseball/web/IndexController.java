package com.example.baseball.web;

import com.example.baseball.repository.TeamRepository;
import com.example.baseball.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final TeamService teamService;

    @GetMapping("/")
    public String index() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signUp")
    public String string(Model model) {
        model.addAttribute("teamList", teamService.selectTeamDtoList());
        return "signUp";
    }

}
