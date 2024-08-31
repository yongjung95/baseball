package com.example.baseball.web;

import com.example.baseball.dto.TeamDto;
import com.example.baseball.service.TeamService;
import com.example.baseball.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final TeamService teamService;
    private final JwtUtil jwtUtil;

    @ModelAttribute
    public void addCommonAttributes(Model model, HttpServletRequest request) {
        // 로그인 여부
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken));
        model.addAttribute("isLoggedIn", isLoggedIn);

        // 팀 목록
        model.addAttribute("teamList", teamService.selectTeamDtoList());
    }

    @GetMapping("/")
    public String index() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/sign-up")
    public String string(Model model) {
        return "signUp";
    }

    @GetMapping("/board/{symbol}")
    public String board(@PathVariable(value = "symbol") String symbol , Model model) {
        if (StringUtils.hasText(symbol)) {
            TeamDto.SelectTeamDto dto = teamService.selectTeamBySymbol(symbol);
            model.addAttribute("teamName", dto.getTeamName());
        }
        return "board/postList";
    }

    @GetMapping("/board/post")
    public String boardPost() {
        return "board/postWrite";
    }

}
