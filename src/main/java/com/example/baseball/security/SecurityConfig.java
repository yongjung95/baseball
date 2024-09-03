package com.example.baseball.security;

import com.example.baseball.response.service.ResponseService;
import com.example.baseball.service.CustomUserDetailsService;
import com.example.baseball.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public static final String[] AUTH_WHITELIST = {
            "/js/**", "/css/**", "/img/**", "/", "/login", "/sign-up", "/member", "check-id", "/check-email", "/check-nickname",
            "/board/**", "/h2-console/**", "/favicon.ico", "/error/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ApplicationContext context) throws Exception {
        // CSRF 보호 비활성화 : CSRF 토큰을 사용하지 않을 것이므로 확인하지 않도록 설정
        http.csrf(AbstractHttpConfigurer::disable);
        // CORS 설정을 적용 : 다른 도메인의 웹 페이지에서 리소스에 접근할 수 있도록 허용
        http.cors(Customizer.withDefaults());

        //세션 관리 상태 없음으로 구성, Spring Security 가 세션 생성 or 사용 X
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));

        //FormLogin, BasicHttp 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        //JwtAuthFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(new JwtAuthFilter(customUserDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling((exceptionHandling) -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint)
        );

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated());
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)); // h2-console iframe 허용
        http.logout(logout -> logout.deleteCookies("token")
                .invalidateHttpSession(true) // 세션 무효화
                .logoutSuccessUrl("/"));
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}