package org.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.entity.LoginInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // 生成密钥
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 生成token
    public String generateToken(LoginInfo loginInfo) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("account", loginInfo.getAccount());
        claims.put("rank", loginInfo.getRank());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(loginInfo.getAccount())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 从token中获取用户名
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // 从token中获取权限
    public String getRankFromToken(String token) {
        return (String) getClaimsFromToken(token).get("rank");
    }

    // 验证token
    public boolean validateToken(String token, LoginInfo loginInfo) {
        final String username = getUsernameFromToken(token);
        return (username.equals(loginInfo.getAccount()) && !isTokenExpired(token));
    }

    // 获取token中的声明
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 检查token是否过期
    public boolean isTokenExpired(String token) {
        final Date expirationDate = getClaimsFromToken(token).getExpiration();
        return expirationDate.before(new Date());
    }
}