package com.vibe.realtime.auth.dto;

import com.vibe.realtime.user.dto.UserResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "AuthResponse", description = "AT, RT 및 사용자 정보 Response DTO")
public class AuthResponse {
	
	@Schema(description = "JWT 토큰 정보", implementation = TokenResponse.class)
    private TokenResponse token;
	
	@Schema(description = "사용자 정보", implementation = UserResponse.class)
    private UserResponse user;
    
}