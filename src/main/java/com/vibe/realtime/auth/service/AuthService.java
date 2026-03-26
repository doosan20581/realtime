package com.vibe.realtime.auth.service;

import java.util.stream.Collectors;

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

	public AuthResponse signup(SignupRequest request) {

	    User user = userService.createUser(
	        request.getEmail(),
	        request.getPassword(),
	        request.getUsername()
	    );
	    
	    // roles 포함 JWT 생성
	    String accessToken = jwtProvider.createAccessToken(
	        user.getId(),
	        user.getRoles().stream()
	            .map(role -> new SimpleGrantedAuthority(role.getName()))
	            .collect(Collectors.toList())
	    );

	    TokenResponse tokenResponse = TokenResponse.builder()
	            .accessToken(accessToken)
	            .refreshToken(null)
	            .expiresIn(jwtProvider.getAccessTokenExpirySeconds())
	            .build();

	    UserResponse userResponse = UserResponse.from(user);

	    return AuthResponse.builder()
	            .token(tokenResponse)
	            .user(userResponse)
	            .build();

	}
	
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

        // 3️-1. ACCESS TOKEN 생성
        String accessToken = jwtProvider.createAccessToken(
            userDetails.getUserId(),
            userDetails.getAuthorities()
        );
        
        // 3-2. (추가) REFRESH TOKEN 생성
        // 작업 필요
        // 현재 aws 기본 환경 세팅 완료 (ec2, elasticache redis oss)
        // 로컬 개발시 로컬 pc 에서 ssh 터널링으로 6379 포트 redis 통신 예정, 로컬 작업지에 ec2 ssh 접속용 개인키 설치 확인

        // 3-3 JWT 생성
        TokenResponse tokenResponse = TokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(null) // (추가) 현재는 null 이지만, 이제 refresh token 작성하는 부분 작업하여서 처리 필요
            .expiresIn(jwtProvider.getAccessTokenExpirySeconds())
            .build();

        // 4️. UserResponse 생성
        UserResponse userResponse = UserResponse.builder()
            .id(userDetails.getUserId())
            .username(userDetails.getUsername())
            .email(userDetails.getUsername())
            .roles(userDetails.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList()))
            .build();

        // 5️. AuthResponse 반환
        return AuthResponse.builder()
            .token(tokenResponse)
            .user(userResponse)
            .build();
    }
	
}
