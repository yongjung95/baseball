package com.example.baseball.util;

import com.example.baseball.dto.MemberDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;


@Slf4j
@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenExpTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration_time}") long accessTokenExpTime
    ) {
        this.key = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        this.accessTokenExpTime = accessTokenExpTime;
    }

    /**
     * Access Token 생성
     *
     * @param dto
     * @return Access Token String
     */
    public String createAccessToken(MemberDto.ResponseMemberDto dto) {
        return createToken(dto, accessTokenExpTime);
    }


    /**
     * JWT 생성
     *
     * @param dto
     * @param expireTime
     * @return JWT String
     */
    private String createToken(MemberDto.ResponseMemberDto dto, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("memberId", dto.getMemberId());
        claims.put("teamName", dto.getTeamName());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * Token에서 User ID 추출
     *
     * @param token
     * @return User ID
     */
    public String getMemberId(String token) {
        return parseClaims(token).get("memberId", String.class);
    }

    /**
     * Token에서 Team Name 추출
     *
     * @param token
     * @return User ID
     */
    public String getTeamName(String token) {
        return parseClaims(token).get("teamName", String.class);
    }


    /**
     * JWT 검증
     *
     * @param token
     * @return IsValidate
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (IllegalArgumentException | JwtException e) {
            return false;
        }
    }


    /**
     * JWT Claims 추출
     *
     * @param accessToken
     * @return JWT Claims
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
