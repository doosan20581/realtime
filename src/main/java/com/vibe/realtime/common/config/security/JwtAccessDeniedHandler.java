package com.vibe.realtime.common.config.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibe.realtime.common.exception.ErrorCode;
import com.vibe.realtime.common.response.CommonResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 필터 레벨 403 응답 포맷 커스텀 처리 - 인가 실패(권한 부족)
 */
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	
	// ObjectMapper를 주입받아 사용하세요
	private final ObjectMapper objectMapper;
	
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        // GlobalExceptionHandler와 동일한 응답 구조 유지
        CommonResponse<Object> commonResponse = CommonResponse.fail(ErrorCode.FORBIDDEN);
        String result = objectMapper.writeValueAsString(commonResponse);
        
        response.getWriter().write(result);
    }
}
