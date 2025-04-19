package com.example.token;

import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.Jwts.parser;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {

    // 토큰 생성시 보안키

    final String KEYCODE = "felkj34#$#_%fejkr4352o9ui432908u432jfi2oj23r53232";
    byte[] securityKeyBytes = Base64.getEncoder().encode(KEYCODE.getBytes());
    SecretKey key = Keys.hmacShaKeyFor(securityKeyBytes);

    // JWT Parser 재사용
    final JwtParser jwtParser = parser().verifyWith(key).build();

    // 토큰 만료시간 설정 / Time 4 Hour
    LocalDateTime expireAt = LocalDateTime.now().plusHours(4);
    Date expiredAtDate = Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant());

    // 로그인 후 JWT 생성
    public String createTokenForAdmin(String adminEmail, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminEmail", adminEmail);
        claims.put("role", role);
        return createToken(claims);
    }

    public String createToken(Map<String, Object> map) {
        // map에 있는 내용을 key에 있는 암호키를 이용해 HS256 알고리즘으로 4시간짜리 토큰 생성
        return builder()

                .setClaims(map)
                .setSubject((String) map.get("adminEmail"))
                .setExpiration(Date.from(LocalDateTime.now().plusHours(4)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validate(String token) {
        try {
            Jws<Claims> result = jwtParser.parseSignedClaims(token);
            return result.getPayload();

        } catch (Exception e) {
            if (e instanceof SignatureException) {
                throw new RuntimeException("유효하지 않은 토큰입니다.");
            } else if (e instanceof ExpiredJwtException) {
                throw new RuntimeException("만료시간이 종료 되었습니다.");
            } else {
                throw new RuntimeException("사용불가 토큰입니다.");
            }
        }

    }

}
