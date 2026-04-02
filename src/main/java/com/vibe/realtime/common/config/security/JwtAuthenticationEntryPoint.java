package com.vibe.realtime.common.config.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibe.realtime.common.exception.ErrorCode;
import com.vibe.realtime.common.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 필터 레벨 401 응답 포맷 커스텀 처리 - JWT 만료 등
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	// ObjectMapper를 주입받아 사용하세요
	private final ObjectMapper objectMapper;
	
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException ex) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        // GlobalExceptionHandler와 동일한 응답 구조 유지
        ApiResponse<Object> apiResponse = ApiResponse.fail(ErrorCode.UNAUTHORIZED);
        String result = objectMapper.writeValueAsString(apiResponse);
        
        response.getWriter().write(result);
    }
}