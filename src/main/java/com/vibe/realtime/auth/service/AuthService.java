package com.vibe.realtime.auth.service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.vibe.realtime.auth.dto.LoginRequest;
import com.vibe.realtime.auth.dto.SignupRequest;
import com.vibe.realtime.auth.dto.TokenResponse;
import com.vibe.realtime.auth.security.CustomUserDetails;
import com.vibe.realtime.common.config.security.JwtProvider;
import com.vibe.realtime.common.exception.BusinessException;
import com.vibe.realtime.common.exception.ErrorCode;
import com.vibe.realtime.common.util.RequestUtil;
import com.vibe.realtime.user.entity.User;
import com.vibe.realtime.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
	
	private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate; // Value를 Object로 선언해야 Map, DTO 등이 허용됨
    private final HttpServletRequest httpServletRequest; // 주입 가능
    
    /**
     * 신규 회원가입
     * 
     * @param request
     * @return
     */
	public TokenResponse signup(SignupRequest request) {
		
		// 1. 신규 회원 레코드 생성
	    User user = userService.createUser(
	        request.getEmail(),
	        request.getPassword(),
	        request.getName()
	    );
	    
	    // 2. 권한 정보 가공
	    List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
	            .map(role -> new SimpleGrantedAuthority(role.getName()))
	            .collect(Collectors.toList());
	    
	    // 3. 통합 메서드 호출 (토큰 생성 + Redis 저장)
	    TokenResponse tokenResponse = generateTokenSet(user.getId(), authorities);
	    
	    // 4. TokenResponse 반환
	    return tokenResponse;
	}
	
	/**
	 * 회원 로그인
	 * 
	 * @param request
	 * @return
	 */
	public TokenResponse login(LoginRequest request) {
		
		String email = request.getEmail();
		String password = request.getPassword();
		
		try {
			
			// 1️. 인증
			// Manager가 Provider -> UserDetailsService 순으로 호출
			// 내부적으로 passwordEncoder.matches()까지 알아서 체크
			// 아이디가 없으면 UsernameNotFoundException, 비밀번호가 틀리면 BadCredentialsException이 발생
			
			// **AuthenticationManager**가 등록된 **DaoAuthenticationProvider**를 깨웁니다.
			// **DaoAuthenticationProvider**는 "DB에 있는 유저 정보를 가져와봐"라며 **CustomUserDetailsService.loadUserByUsername(email)**을 호출합니다. (이때 실행됩니다!)
			// loadUserByUsername이 DB에서 정보를 읽어와 **CustomUserDetails (Full 버전)**을 리턴합니다.
			// Provider는 리턴받은 userDetails의 비밀번호와 사용자가 입력한 password를 passwordEncoder.matches()로 비교합니다.
			// 일치하면 인증된 Authentication 객체를 리턴하고, 틀리면 BadCredentialsException을 던집니다.
			
	        Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(email, password)
	        );
	        
	        // [필수] 로그인 성공 후, 해당 요청 흐름에서 사용할 인증 정보를 Spring Security 컨텍스트에 저장
	        // 이후 매 요청시마다, jwt 필터에서 검증 후 Spring Security 컨텍스트에 저장
	        SecurityContextHolder.getContext().setAuthentication(authentication);

	        // 2️. CustomUserDetails 추출
	        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	        
	        // 3. 통합 메서드 호출 (토큰 생성 + Redis 저장)
		    TokenResponse tokenResponse = generateTokenSet(userDetails.getUserId(), userDetails.getAuthorities());

	        // 4. TokenResponse 반환
	        return tokenResponse;
			
		} catch (AuthenticationException e) {
			// [핵심] 아이디/비밀번호 불일치 등 모든 인증 실패를 LOGIN_FAILED로 치환
	        log.warn("Login failed for user: {}", request.getEmail()); // 보안상 필요시 로그 기록
	        throw new BusinessException(ErrorCode.LOGIN_FAILED);
	        
	        /*
	         * AuthenticationException (부모)
			 * UsernameNotFoundException: 계정을 찾을 수 없을 때 (아이디 틀림)
			 * BadCredentialsException: 자격 증명이 유효하지 않을 때 (비밀번호 틀림)
   			 * LockedException: 계정이 잠겨 있을 때
   			 * DisabledException: 계정이 비활성화 상태일 때
   			 * AccountExpiredException: 계정이 만료되었을 때
	         */
		}
		
    }
	
	/**
	 * AT + RT 토큰 세트 생성 및 리프레시 토큰 Redis 저장 통합 메서드
	 */
	private TokenResponse generateTokenSet(Integer userId, Collection<? extends GrantedAuthority> authorities) {
		/**
	     * [Refresh Token 관리 구조]
	     * 1. Key: RT:{user_id}:{jti}
	     * - user_id: 사용자 식별자 (Integer)
	     * - jti: 토큰 고유 UUID (String)
	     * - 목적: 멀티 디바이스 로그인 허용 및 각 세션의 독립적 제어
	     * 2. Value: JSON String (Map<String, String>)
	     * - ip: 로그인 시점의 클라이언트 IP (보안 검증용)
	     * - userAgent: 접속 기기/브라우저 정보 (세션 관리용)
	     * - jti: 토큰 식별자
	     * 3. TTL: 리프레시 토큰 유효 기간과 동일 (1일)
	     * 
	     * [Elacticache Redis Oss]
	     * 현재 aws 기본 환경 세팅 완료 (ec2, elasticache redis oss)
	     * 로컬 개발시 로컬 pc 에서 ssh 터널링으로 6379 포트 redis 통신 예정, 로컬 작업지에 ec2 ssh 접속용 개인키 설치 확인
	     * 
	     */
		
		// 1. Access Token 생성 (jti 포함)
	    String accessJti = UUID.randomUUID().toString();
	    String accessToken = jwtProvider.createAccessToken(userId, authorities, accessJti);

	    // 2. Refresh Token 생성 (jti 포함)
	    String refreshJti = UUID.randomUUID().toString();
	    String refreshToken = jwtProvider.createRefreshToken(userId, refreshJti);

	    // 3. Redis 저장 (구조화된 Key/Value 사용)
	    // Key=RT:1:uuid... | Value={"ip":"...","userAgent":"...","jti":"..."}
	    String redisKey = "RT:" + userId + ":" + refreshJti;
	    Map<String, String> refreshValue = Map.of(
	        "ip", RequestUtil.getClientIp(httpServletRequest),
	        "userAgent", RequestUtil.getUserAgent(httpServletRequest),
	        "jti", refreshJti
	    );

	    redisTemplate.opsForValue().set(
	        redisKey,
	        refreshValue,
	        Duration.ofMillis(jwtProvider.getRefreshTokenExpirySeconds() * 1000)
	    );

	    // 4. 결과 반환
	    return TokenResponse.builder()
	            .accessToken(accessToken)
	            .refreshToken(refreshToken)
	            .expiresIn(jwtProvider.getAccessTokenExpirySeconds())
	            .build();
	}
	
}