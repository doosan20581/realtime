package com.vibe.realtime.common.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibe.realtime.common.exception.ErrorCode;
import com.vibe.realtime.common.response.CommonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 다음 필터(JwtAuthenticationFilter)로 요청 전달
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // 1. 토큰 만료 시
        	// ExpiredJwtException: JwtProvider에서 parseClaimsJws()를 호출할 때, 현재 시간이 토큰의 exp 시간을 지났다면 발생하는 예외입니다. 이를 캐치하여 EXPIRED_TOKEN을 보냅니다.
            log.warn("JWT Token Expired: {}", e.getMessage());
            setErrorResponse(response, ErrorCode.EXPIRED_TOKEN);
            
        } catch (MalformedJwtException | SignatureException | IllegalArgumentException e) {
            // 2. 토큰 변조 및 형식이 잘못된 경우
        	// SignatureException: 서버의 시크릿 키로 풀리지 않는(변조된) 토큰일 때 발생합니다.
        	// MalformedJwtException: JWT의 구조(header.payload.signature)가 깨져있을 때 발생합니다.
            log.warn("Invalid JWT Token: {}", e.getMessage());
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);
        } catch (JwtException e) {
            // 3. 그 외 기타 JWT 관련 예외
            log.warn("JWT Exception: {}", e.getMessage());
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");

        // 프로젝트 공통 응답 포맷 사용
        CommonResponse<Object> commonResponse = CommonResponse.fail(errorCode);
        String result = objectMapper.writeValueAsString(commonResponse);

        response.getWriter().write(result);
    }
}