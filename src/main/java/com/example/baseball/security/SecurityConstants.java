package com.example.baseball.security;

public class SecurityConstants {
    public static final String[] AUTH_WHITELIST = {
            "/js/**", "/css/**", "/img/**", "/", "/login", "/sign-up", "/member", "/check-email", "/check-nickname",
            "/board/**", "/h2-console/**", "/favicon.ico", "/error/**"
    };
}

