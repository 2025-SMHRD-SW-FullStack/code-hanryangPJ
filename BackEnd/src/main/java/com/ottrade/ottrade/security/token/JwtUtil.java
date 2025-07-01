package com.ottrade.ottrade.security.token;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ottrade.ottrade.domain.member.entity.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {
	
	
	

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰생성하는곳 
    
    
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    //AccessToken 토큰생성 
    public String generateAccessToken(Long userId, Role role) {
    	
    	// 토큰에 담을 정보(Claims) 설정
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("role", role.getKey()); // 권한 정보 추가

        // 현재 시간 및 만료 시간 설정
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);

        // JWT 빌더를 사용하여 토큰 생성
        return Jwts.builder()
                .setClaims(claims) // 정보(Claims) 설정
                .setIssuedAt(now) // 토큰 발급 시간
                .setExpiration(expiryDate) // 토큰 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 서명
                .compact();
    }
    

    //RefreshToken 토큰생성 
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰을 꺼내는 메서드
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거
        }
        return null;
    }

    // 토큰유효기간 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

     //토큰 Claims (조각데이터) 반환
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰사용자 id추출
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(getClaimsFromToken(token).getSubject());
    }
    
    
 
    
    
}