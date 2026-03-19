package com.vibe.realtime.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TokenResponse", description = "JWT 토큰 반환 DTO")
public class TokenResponse {
	
	@Schema(description = "클라이언트 인증용 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;    // 클라이언트 인증용 JWT
	
	@Schema(description = "액세스 토큰 만료 시 재발급용 리프레시 토큰", example = "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...")
    private String refreshToken;   // 만료 시 재발급용 JWT
    
	@Schema(description = "액세스 토큰 만료 시간(초 단위)", example = "3600")
	private Long expiresIn;        // 액세스 토큰 만료 시간 (초 단위)
    
}
