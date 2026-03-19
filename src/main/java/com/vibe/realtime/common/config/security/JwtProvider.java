package com.vibe.realtime.common.config.security;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.vibe.realtime.user.entity.Role;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * JWT Provider (토이 프로젝트용)
 *
 * - In-memory 랜덤 키 사용: 서버 재시작 시 키가 변경됨
 *   → 기존 토큰은 검증 불가
 * - 실제 서비스에서는 환경변수/설정 파일에 고정 시크릿 키(jwt.secret) 사용 필요
 */
@Component
public class JwtProvider {
	
	// 토이 프로젝트용 In-Memory JWT Key
	// application.yaml 또는 환경 변수로 고정된 시크릿 키 사용방식을, 간소화 처리
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 시크릿 키 자동 생성
    private final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 60; // 1시간

    // Access Token 생성 = JWT 발급
    // 핵심: JWT 생성 시 DB 엔티티(Role)에 직접 접근하지 않음
    public String createAccessToken(Integer userId, Collection<? extends GrantedAuthority> authorities) {
    	Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);
    	
    	List<String> roleNames = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("roles", roleNames)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // ----------------------------
    // 토큰 유효성 검사 = 토큰 유효성 확인 (만료, 변조 등)
    // ----------------------------
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // 만료/위조 등
        }
    }

    // ----------------------------
    // 토큰에서 사용자 ID 추출 = 토큰에서 사용자 식별 정보 추출
    // ----------------------------
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject()
        );
    }
    
    // ----------------------------
    // 토큰에서 role 목록 추출
    // 토큰에서 역할(claim) 추출
    // ----------------------------
    public List<String> getRolesFromToken(String token) {
        Object rolesObj = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles");

        // Object → List<String> 안전 변환
        // claim이 리스트인지 확인 -> 내부 요소가 String인지 체크 후 안전하게 변환
        if (rolesObj instanceof List<?>) { 
            List<?> rawList = (List<?>) rolesObj;
            List<String> roleNames = new ArrayList<>();
            for (Object obj : rawList) {
                if (obj instanceof String) {
                    roleNames.add((String) obj);
                }
            }
            return roleNames;
        }

        return new ArrayList<>();
    }
    
    // ----------------------------
    // ACCESS 토큰 만료시간 리턴
    // ----------------------------
    public long getAccessTokenExpirySeconds() {
        return ACCESS_TOKEN_VALIDITY / 1000L; // 밀리초 → 초 단위
    }
}
