package com.vibe.realtime.auth.dto;

import com.vibe.realtime.user.dto.UserResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "SignupResponse", description = "회원가입 요청 응답용 AT 및 사용자 정보 Response DTO")
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 외부에서 new SignupResponse(...) 호출 방지
public class SignupResponse {
	
	@Schema(description = "클라이언트 인증용 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;    // 클라이언트 인증용 JWT
    
	@Schema(description = "액세스 토큰 만료 시간(초 단위)", example = "3600")
	private Long expiresIn;        // 액세스 토큰 만료 시간 (초 단위)
	
	@Schema(description = "사용자 정보", implementation = UserResponse.class)
    private UserResponse user;
	
	// -----------------------------
    // AuthResponse 엔티티 → LoginResponse 변환 (정적 팩토리 메서드)
    // -----------------------------
    public static SignupResponse from(AuthResponse authResponse) {
        return SignupResponse.builder()
                .accessToken(authResponse.getToken().getAccessToken())
                .expiresIn(authResponse.getToken().getExpiresIn())
                .user(authResponse.getUser())
                .build();
    }
}