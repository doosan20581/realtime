package com.vibe.realtime.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vibe.realtime.auth.dto.LoginRequest;
import com.vibe.realtime.auth.dto.LoginResponse;
import com.vibe.realtime.auth.dto.SignupRequest;
import com.vibe.realtime.auth.dto.SignupResponse;
import com.vibe.realtime.auth.dto.TokenResponse;
import com.vibe.realtime.auth.security.CustomUserDetails;
import com.vibe.realtime.auth.service.AuthService;
import com.vibe.realtime.common.annotation.ApiCommonResponses;
import com.vibe.realtime.common.config.security.JwtProvider;
import com.vibe.realtime.common.response.CommonResponse;
import com.vibe.realtime.user.dto.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
	
	private final AuthService authService;
	private final JwtProvider jwtProvider;
	
	@PostMapping("/signup")
	@Operation(summary = "회원가입 및 JWT 발급", description = "회원가입 처리 후 AT는 Body, RT는 Cookie로 발급합니다.")
	@ApiCommonResponses
	public ResponseEntity<CommonResponse<SignupResponse>> signup(@RequestBody @Valid SignupRequest request) {
	    
		// 회원가입 로직 수행 (AT, RT 생성 및 Redis 저장)
	    TokenResponse tokenResponse = authService.signup(request);
	    
	    return toResponseEntity(SignupResponse.from(tokenResponse), tokenResponse.getRefreshToken());
	}
	
	@PostMapping("/login")
	@Operation(summary = "로그인", description = "ID/PW로 로그인하고 AT는 Body, RT는 Cookie로 발급합니다.")
	@ApiCommonResponses
	public ResponseEntity<CommonResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
		// 로그인 로직 수행 (AT, RT 생성 및 Redis 저장)
	    TokenResponse tokenResponse = authService.login(request);
	    
	    return toResponseEntity(LoginResponse.from(tokenResponse), tokenResponse.getRefreshToken());
	}
	
	@GetMapping("/me")
    @Operation(summary = "JWT로 인증된 현재 로그인 사용자의 기본 정보 조회", description = "JWT를 이용하여 현재 로그인한 사용자의 기본 정보를 반환합니다.")
	@ApiCommonResponses
    public CommonResponse<UserResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        
		// CustomUserDetails에서 필요한 정보 추출
        UserResponse userResponse = UserResponse.builder()
                .id(userDetails.getUserId())
                .name(userDetails.getName())
                .email(userDetails.getEmail())
                .roles(userDetails.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .toList())
                .build();

        return CommonResponse.success(userResponse);
    }
	
	/**
	 * 응답 공통 조립 메서드 (Generic 활용)
	 * AT는 Body, RT는 ResponseCookie로 발급
	 */
	private <T> ResponseEntity<CommonResponse<T>> toResponseEntity(T body, String refreshToken) {
	    ResponseCookie rtCookie = createRefreshTokenCookie(refreshToken);
	    
	    return ResponseEntity.ok()
	            .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
	            .body(CommonResponse.success(body));
	}
	
	/**
	 * Refresh Token 전용 쿠키 생성 로직 추출
	 */
	private ResponseCookie createRefreshTokenCookie(String refreshToken) {
	    return ResponseCookie.from("refreshToken", refreshToken)
	            .httpOnly(true)
	            .secure(true)
	            .path("/")
	            .maxAge(jwtProvider.getRefreshTokenExpirySeconds())
	            .sameSite("Strict") // CSRF 방지를 위해 추가 권장
	            .build();
	}
}
