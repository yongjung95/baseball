package com.example.baseball.web;

import com.example.baseball.dto.PostDto;
import com.example.baseball.dto.TeamDto;
import com.example.baseball.service.PostService;
import com.example.baseball.service.TeamService;
import com.example.baseball.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final PostService postService;
    private final JwtUtil jwtUtil;

    @ModelAttribute
    public void addCommonAttributes(Model model) {
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
    public String board(@PathVariable(value = "symbol") String symbol,
                        @CookieValue(value = "token", required = false) String token,
                        PostDto.SelectPostRequestDto requestDto, Model model) {
        if (StringUtils.hasText(symbol)) {
            TeamDto.SelectTeamDto dto = teamService.selectTeamBySymbol(symbol);
            model.addAttribute("symbol", dto.getSymbol());
            model.addAttribute("teamName", dto.getTeamName());

            PageRequest pageRequest = PageRequest.of(requestDto.getPage(), requestDto.getSize(), Sort.by(Sort.Direction.DESC, "createdDate"));
            Page<PostDto.ResponsePostDto> postList = postService.selectPostList(requestDto.getSearchText(), dto.getTeamId(), pageRequest);
            model.addAttribute("postList", postList);

            boolean isSymbolMatched = false;
            if (StringUtils.hasText(token)) {
                String teamName = jwtUtil.getTeamName(token);

                if (dto.getTeamName().equals(teamName)) {
                    isSymbolMatched = true;
                }
            }

            model.addAttribute("isSymbolMatched", isSymbolMatched);
        }
        return "board/postList";
    }

    @GetMapping("/board/post")
    public String boardPost() {
        return "board/postWrite";
    }

    @GetMapping("/board/post/{postId}")
    public String boardPost(@PathVariable(name = "postId") Long postId, Model model) {
        PostDto.ResponsePostDto responsePostDto = postService.selectPost(postId);
        model.addAttribute("post", responsePostDto);
        return "board/postDetail";
    }
}
