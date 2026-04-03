package com.vibe.realtime.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoginRequest", description = "로그인 요청 DTO")
public class LoginRequest {
	
	// 1. 실제 검증: 필수값, 이메일 형식
	// 2. 문서 가이드: 필수값, 이메일 형식임을 명시
	@NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @Schema(description = "사용자 이메일", example = "tester@example.com", format = "email", required = true)
	String email;
	
	// 1. 실제 검증: 필수값, 6자 이상 ~ 20자 이하
	// 2. 문서 가이드: 필수값, 6자 이상, 20자 이하임을 명시
	@NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, max = 20, message = "비밀번호는 6자 ~ 20자 이어야 합니다.")
    @Schema(description = "비밀번호", example = "123456", minLength = 6, maxLength = 20, required = true)
	String password;
	
}
