package com.example.baseball.security;

import com.example.baseball.response.error.ErrorCode;
import com.example.baseball.response.service.ResponseService;
import com.example.baseball.service.CustomUserDetailsService;
import com.example.baseball.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final ResponseService responseService;

    @Override
    /**
     * JWT 토큰 검증 필터 수행
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getCookieValue(request, "token"); // 쿠키에서 JWT 토큰 조회

        //JWT가 헤더에 있는 경우
        if (StringUtils.hasText(token)) {
            //JWT 유효성 검증
            if (jwtUtil.validateToken(token)) {
                String memberId = jwtUtil.getMemberId(token);

                //유저와 토큰 일치 시 userDetails 생성
                try {
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(memberId);
                    if (userDetails != null) {
                        //UserDetsils, Password, Role -> 접근권한 인증 Token 생성
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        //현재 Request의 Security Context에 접근권한 설정
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                } catch (UsernameNotFoundException e) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.sendRedirect("/");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response); // 다음 필터로 넘기기
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
