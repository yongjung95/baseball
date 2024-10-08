package com.example.baseball.web;

import com.example.baseball.dto.PostDto;
import com.example.baseball.dto.TeamDto;
import com.example.baseball.response.exception.ResourceNotFoundException;
import com.example.baseball.service.MemberService;
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
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @ModelAttribute
    public void addCommonAttributes(@CookieValue(value = "token", required = false) String token, Model model) {
        // 로그인 여부
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken));
        model.addAttribute("isLoggedIn", isLoggedIn);
        String memberId = null;
        if (jwtUtil.validateToken(token)) {
            memberId = jwtUtil.getMemberId(token);
        }
        model.addAttribute("memberId", memberId);

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

    @GetMapping("/member/detail")
    public String memberDetail(@CookieValue(value = "token") String token, Model model) {
        String memberId = jwtUtil.getMemberId(token);

        model.addAttribute("member", memberService.selectMemberByMemberId(memberId));

        return "member/detail";
    }

    @GetMapping("/member/edit")
    public String memberEdit(@CookieValue(value = "token") String token, Model model) {
        String memberId = jwtUtil.getMemberId(token);

        model.addAttribute("member", memberService.selectMemberByMemberId(memberId));

        return "member/edit";
    }

    @GetMapping("/member/password")
    public String memberPassword() {
        return "member/password";
    }


    @GetMapping("/board/{symbol}")
    public String board(@PathVariable(value = "symbol") String symbol,
                        @CookieValue(value = "token", required = false) String token,
                        PostDto.SelectPostRequestDto requestDto, Model model) {
        if (StringUtils.hasText(symbol)) {
            TeamDto.SelectTeamDto dto = teamService.selectTeamBySymbol(symbol);
            model.addAttribute("symbol", dto.getSymbol());
            model.addAttribute("logo", dto.getLogo());

            model.addAttribute("teamName", dto.getTeamName());

            PageRequest pageRequest = PageRequest.of(requestDto.getPage(), requestDto.getSize(), Sort.by(Sort.Direction.DESC, "createdDate"));
            Page<PostDto.SelectPostListDto> postList = postService.selectPostList(requestDto.getSearchText(), dto.getTeamId(), pageRequest);
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
    public String boardPost(@CookieValue(value = "token", required = false) String token) {
        if (!jwtUtil.validateToken(token)) {
            return "redirect:/";
        }
        return "board/postWrite";
    }

    @GetMapping("/board/post/{postId}")
    public String boardPost(@PathVariable(name = "postId") Long postId,
                            @CookieValue(value = "token", required = false) String token, Model model) {
        PostDto.ResponsePostDto responsePostDto = postService.selectPost(postId);

        if (responsePostDto == null) {
            throw new ResourceNotFoundException("존재 하지 않습니다.");
        }
        model.addAttribute("post", responsePostDto);

        boolean isAuthor = false;
        String memberId = null;
        if (StringUtils.hasText(token)) {
             memberId = jwtUtil.getMemberId(token);

            if (responsePostDto.getAuthorId().equals(memberId)) {
                isAuthor = true;
            }
        }

        model.addAttribute("isAuthor", isAuthor);
        model.addAttribute("memberId", memberId);
        return "board/postDetail";
    }

    @GetMapping("/board/post/{postId}/edit")
    public String boardPostEdit(@PathVariable(name = "postId") Long postId,
                                @CookieValue(value = "token", required = false) String token, Model model) {
        PostDto.ResponsePostDto responsePostDto = postService.selectPost(postId);
        model.addAttribute("post", responsePostDto);

        if (StringUtils.hasText(token)) {
            String memberId = jwtUtil.getMemberId(token);

            if (responsePostDto.getAuthorId().equals(memberId)) {
                return "board/postEdit";
            }
        }

        return "redirect:/";
    }
}
