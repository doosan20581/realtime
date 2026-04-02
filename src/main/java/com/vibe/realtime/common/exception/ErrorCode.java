package com.vibe.realtime.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    
	// --- 기존 사용 중인 비즈니스 에러 ---
	EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "이미 존재하는 이메일입니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD("INVALID_PASSWORD", "비밀번호가 올바르지 않습니다."),
    ROLE_NOT_FOUND("ROLE_NOT_FOUND", "권한을 찾을 수 없습니다."),
	
	// --- 인증 및 인가 (추가 제안) ---
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다."), // 401
    FORBIDDEN("FORBIDDEN", "접근 권한이 없습니다."), // 403
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN("EXPIRED_TOKEN", "만료된 토큰입니다."),

    // --- 기타 공통 에러 (추가 제안) ---
    INVALID_INPUT_VALUE("INVALID_INPUT_VALUE", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다.")
    
    ;

    private final String code;
    private final String message;
    
}
