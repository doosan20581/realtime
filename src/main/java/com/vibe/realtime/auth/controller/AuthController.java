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

import com.vibe.realtime.auth.dto.AuthResponse;
import com.vibe.realtime.auth.dto.LoginRequest;
import com.vibe.realtime.auth.dto.LoginResponse;
import com.vibe.realtime.auth.dto.SignupRequest;
import com.vibe.realtime.auth.dto.SignupResponse;
import com.vibe.realtime.auth.security.CustomUserDetails;
import com.vibe.realtime.auth.service.AuthService;
import com.vibe.realtime.common.config.security.JwtProvider;
import com.vibe.realtime.common.response.ApiResponse;
import com.vibe.realtime.user.dto.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
	@Operation(
	    summary = "회원가입 및 JWT 발급",
	    description = "신규 회원 가입 후 AuthResponse를 반환합니다."
	)
	@ApiResponses({
	    // 1. 200: 성공
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "200",
	        description = "회원가입 성공",
	        content = @Content(schema = @Schema(implementation = AuthResponse.class))
	    ),
	    // 2. 400: 이메일 중복 또는 Validation 에러
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "400",
	        description = "잘못된 요청 (이메일 중복 또는 유효성 위반)",
	        content = @Content(schema = @Schema(implementation = ApiResponse.class))
	    ),
	    // 3. 500: 서버 에러
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "500",
	        description = "서버 오류",
	        content = @Content(schema = @Schema(implementation = ApiResponse.class))
	    )
	})
	public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody @Valid SignupRequest request) {
	    
		// 1. 회원가입 로직 수행 (AT, RT 생성 및 Redis 저장)
	    AuthResponse authResponse = authService.signup(request);
	    
	    // 2. 쿠키 생성 (RT 전용)
	    ResponseCookie rtCookie = ResponseCookie.from("refreshToken", authResponse.getToken().getRefreshToken()) // RT 추출하여 쿠키 생성
	            .httpOnly(true)
	            .secure(true)
	            .path("/")
	            .maxAge(jwtProvider.getRefreshTokenExpirySeconds())
	            .build();
	    
	    // 3. 회원가입 응답용 DTO로 변환 (RT 제외)
	    SignupResponse signupResponse = SignupResponse.from(authResponse);

	    // 4. 응답 조립
	    // Body에는 ApiResponse.success()를 사용하고, Header에는 쿠키를 추가합니다.
	    return ResponseEntity.ok()
	            .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
	            .body(ApiResponse.success(signupResponse));
	}
	
	@PostMapping("/login")
	@Operation(
	    summary = "로그인 처리",
	    description = "로그인 처리 후 AuthResponse를 반환합니다."
	)
	@ApiResponses({
	    // 1. 200: 성공
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "200",
	        description = "로그인 성공 성공",
	        content = @Content(schema = @Schema(implementation = AuthResponse.class))
	    ),
	    // 2. 400: Validation 에러
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "400",
	        description = "잘못된 요청 (유효성 위반)",
	        content = @Content(schema = @Schema(implementation = ApiResponse.class))
	    ),
	    // 3. 500: 서버 에러
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "500",
	        description = "서버 오류",
	        content = @Content(schema = @Schema(implementation = ApiResponse.class))
	    )
	})
	public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
		// 1. 로그인 로직 수행 (AT, RT 생성 및 Redis 저장)
	    AuthResponse authResponse = authService.login(request);
	    
	    // 2. 쿠키 생성 (RT 전용)
	    ResponseCookie rtCookie = ResponseCookie.from("refreshToken", authResponse.getToken().getRefreshToken()) // RT 추출하여 쿠키 생성
	            .httpOnly(true)
	            .secure(true)
	            .path("/")
	            .maxAge(jwtProvider.getRefreshTokenExpirySeconds())
	            .build();
	    
	    // 3. 로그인 응답용 DTO로 변환 (RT 제외)
	    LoginResponse loginResponse = LoginResponse.from(authResponse);

	    // 4. 응답 조립
	    // Body에는 ApiResponse.success()를 사용하고, Header에는 쿠키를 추가합니다.
	    return ResponseEntity.ok()
	            .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
	            .body(ApiResponse.success(loginResponse));         
	}
	
	@GetMapping("/me")
    @Operation(summary = "JWT로 인증된 현재 로그인 사용자의 기본 정보 조회", description = "JWT를 이용하여 현재 로그인한 사용자의 기본 정보를 반환합니다.")
    public ApiResponse<UserResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // CustomUserDetails에서 필요한 정보 추출
        UserResponse userResponse = UserResponse.builder()
                .id(userDetails.getUserId())
                .name(userDetails.getName())
                .email(userDetails.getEmail())
                .roles(userDetails.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .toList())
                .build();

        return ApiResponse.success(userResponse);
    }
}
