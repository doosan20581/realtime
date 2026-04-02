package com.vibe.realtime.common.response;

import com.vibe.realtime.auth.dto.LoginResponse;
import com.vibe.realtime.auth.dto.SignupResponse;
import com.vibe.realtime.common.exception.ErrorCode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(name = "CommonResponse", description = "API 공통 응답 포맷")
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 외부에서 new CommonResponse(...) 호출 방지
public class CommonResponse<T> {
	
	@Schema(description = "요청 성공 여부", example = "true")
    private boolean success;
	
	@Schema(description = "응답 코드", example = "SUCCESS")
    private String code;
	
	@Schema(description = "응답 메시지", example = "OK")
    private String message;
	
	@Schema(description = "응답 데이터")
    private T data;

    // -----------------------------
    // 성공 응답
    // -----------------------------
    public static <T> CommonResponse<T> success(T data) {
        CommonResponse<T> response = new CommonResponse<>();
        response.success = true;
        response.code = "SUCCESS"; // 기본 코드
        response.message = "OK";   // 기본 메시지
        response.data = data;
        return response;
    }

    // -----------------------------
    // 실패 응답: ErrorCode 기반
    // -----------------------------
    public static <T> CommonResponse<T> fail(ErrorCode errorCode) {
        CommonResponse<T> response = new CommonResponse<>();
        response.success = false;
        response.code = errorCode.getCode();
        response.message = errorCode.getMessage();
        response.data = null;
        return response;
    }

    // -----------------------------
    // 실패 응답: 직접 메시지 지정
    // -----------------------------
    public static <T> CommonResponse<T> fail(String code, String message) {
        CommonResponse<T> response = new CommonResponse<>();
        response.success = false;
        response.code = code;
        response.message = message;
        response.data = null;
        return response;
    }
    
    // -----------------------------
    // 실패 응답: ErrorCode 기반 + 상세 메세지 출력
    // -----------------------------
    public static <T> CommonResponse<T> fail(ErrorCode errorCode, String errorMessage) {
		CommonResponse<T> response = new CommonResponse<>();
        response.success = false;
        response.code = errorCode.getCode();
        response.message = errorMessage;
        response.data = null;
        return response;
	}

    // 기본 생성자는 private으로 숨겨도 됨 (정적 팩토리 사용)
    private CommonResponse() {}
    
    // Swagger 표시용 정적 클래스
    // CommonResponse.java 내부 혹은 별도 파일
    public static class LoginCommonResponse extends CommonResponse<LoginResponse> {}
    public static class SignupCommonResponse extends CommonResponse<SignupResponse> {}
}