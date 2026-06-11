package com.tyut.holiday.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 生成与解析。token 中存放 userId（subject）与 loginName。
 */
@Component
public class JwtUtil {

    private final JwtProperties props;
    private final SecretKey key;

    public JwtUtil(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /** 生成 token，activeRole 为当前激活角色 */
    public String generate(Long userId, String loginName, String activeRole) {
        long now = System.currentTimeMillis();
        Date exp = new Date(now + props.getExpireMinutes() * 60_000L);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("loginName", loginName)
                .claim("activeRole", activeRole)
                .issuedAt(new Date(now))
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    /** 解析并校验 token，返回 Claims；失败抛异常 */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** 从 token 取 userId */
    public Long getUserId(String token) {
        return Long.valueOf(parse(token).getSubject());
    }
}
