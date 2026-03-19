package com.vibe.realtime.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    
	EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "이미 존재하는 이메일입니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD("INVALID_PASSWORD", "비밀번호가 올바르지 않습니다."),
    ROLE_NOT_FOUND("ROLE_NOT_FOUND", "권한을 찾을 수 없습니다.");

    private final String code;
    private final String message;
    
}
