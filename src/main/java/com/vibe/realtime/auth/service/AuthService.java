package com.vibe.realtime.auth.service;

import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.vibe.realtime.auth.dto.AuthResponse;
import com.vibe.realtime.auth.dto.LoginRequest;
import com.vibe.realtime.auth.dto.SignupRequest;
import com.vibe.realtime.auth.dto.TokenResponse;
import com.vibe.realtime.auth.security.CustomUserDetails;
import com.vibe.realtime.common.config.security.JwtProvider;
import com.vibe.realtime.user.dto.UserResponse;
import com.vibe.realtime.user.entity.User;
import com.vibe.realtime.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
	
	private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;
    
    /**
     * 신규 회원가입
     * 
     * @param request
     * @return
     */
	public AuthResponse signup(SignupRequest request) {
		
		// 1. 신규 회원 레코드 생성
	    User user = userService.createUser(
	        request.getEmail(),
	        request.getPassword(),
	        request.getName()
	    );
	    
	    // 2-1. access_jti 생성
	    String access_jti = UUID.randomUUID().toString(); // 토큰 고유 ID
	    
	    // 2-2. ACCESS TOKEN 생성
	    String accessToken = jwtProvider.createAccessToken(
	        user.getId(),
	        user.getRoles().stream()
	            .map(role -> new SimpleGrantedAuthority(role.getName()))
	            .collect(Collectors.toList()),
	        access_jti
	    );
	    
	    // 3-1. refresh_jti 생성
	    String refresh_jti = UUID.randomUUID().toString(); // 토큰 고유 ID
	    
	    // 3-2. REFRESH TOKEN 생성
        // 현재 aws 기본 환경 세팅 완료 (ec2, elasticache redis oss)
        // 로컬 개발시 로컬 pc 에서 ssh 터널링으로 6379 포트 redis 통신 예정, 로컬 작업지에 ec2 ssh 접속용 개인키 설치 확인
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), refresh_jti);
        
        // 3-3. Redis 저장
        String redisKey = "RT:" + user.getId();

        redisTemplate.opsForValue().set(
            redisKey,
            refresh_jti,
            Duration.ofMillis(jwtProvider.getRefreshTokenExpirySeconds() * 1000)
        );
        
        // 4. TokenResponse 생성 - 백엔드용 context
	    TokenResponse tokenResponse = TokenResponse.builder()
	            .accessToken(accessToken)
	            .refreshToken(refreshToken)
	            .expiresIn(jwtProvider.getAccessTokenExpirySeconds())
	            .build();
	    
	    // 5. UserResponse 생성 - 프론트엔드용 context
	    UserResponse userResponse = UserResponse.from(user);
	    
	    // 6. AuthResponse 반환
	    return AuthResponse.builder()
	            .token(tokenResponse)
	            .user(userResponse)
	            .build();

	}
	
	/**
	 * 회원 로그인
	 * 
	 * @param request
	 * @return
	 */
	public AuthResponse login(LoginRequest request) {
		
		String email = request.getEmail();
		String password = request.getPassword();
		
        // 1️. 인증
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2️. CustomUserDetails 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // 3-1. access_jti 생성
	    String access_jti = UUID.randomUUID().toString(); // 토큰 고유 ID

        // 3️-2. ACCESS TOKEN 생성
        String accessToken = jwtProvider.createAccessToken(
            userDetails.getUserId(),
            userDetails.getAuthorities(),
            access_jti
        );
        
        // 4-1. refresh_jti 생성
	    String refresh_jti = UUID.randomUUID().toString(); // 토큰 고유 ID
        
        // 4-2. REFRESH TOKEN 생성
        // 현재 aws 기본 환경 세팅 완료 (ec2, elasticache redis oss)
        // 로컬 개발시 로컬 pc 에서 ssh 터널링으로 6379 포트 redis 통신 예정, 로컬 작업지에 ec2 ssh 접속용 개인키 설치 확인
        String refreshToken = jwtProvider.createRefreshToken(userDetails.getUserId(), refresh_jti);
        
        // 4-3. Redis 저장
        String redisKey = "RT:" + userDetails.getUserId();

        redisTemplate.opsForValue().set(
            redisKey,
            refresh_jti,
            Duration.ofMillis(jwtProvider.getRefreshTokenExpirySeconds() * 1000)
        );

        // 5. TokenResponse 생성 - 백엔드용 context
        TokenResponse tokenResponse = TokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(jwtProvider.getAccessTokenExpirySeconds())
            .build();

        // 6. UserResponse 생성 - 프론트엔드용 context
        UserResponse userResponse = UserResponse.builder()
            .id(userDetails.getUserId())
            .name(userDetails.getName())
            .email(userDetails.getEmail())
            .roles(userDetails.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList()))
            .build();

        // 7. AuthResponse 반환
        return AuthResponse.builder()
            .token(tokenResponse)
            .user(userResponse)
            .build();
    }
	
}
