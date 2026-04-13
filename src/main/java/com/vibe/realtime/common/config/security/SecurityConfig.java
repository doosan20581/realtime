package com.vibe.realtime.common.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.vibe.realtime.auth.security.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // ------------------------------
    // 1. PasswordEncoder Bean
    // ------------------------------
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ------------------------------
    // 2. AuthenticationProvider Bean
    // ------------------------------
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // ------------------------------
    // 3. AuthenticationManager Bean
    // ------------------------------
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ------------------------------
    // 4. SecurityFilterChain Bean
    // ------------------------------
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // REST API이므로 CSRF 비활성화
        	// CSRF(Cross-Site Request Forgery, 사이트 간 요청 위조) 보호 메커니즘을 비활성화
        	// 
            .csrf(csrf -> csrf.disable())

            // JWT 기반이므로 세션 사용 안 함
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // URL 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                //.requestMatchers("/").permitAll() // 테스트
                .requestMatchers("/auth/**").permitAll() // 로그인 관련
                .requestMatchers("/ws/**").permitAll() // 웹소켓
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // 스웨거
                .anyRequest().authenticated()
            )

            // 기본 로그인 UI/HTTP Basic 비활성화
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())

            // 예외 처리
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )

            // AuthenticationProvider 설정
            .authenticationProvider(authenticationProvider(passwordEncoder()))

            // 1. JWT 인증 필터 등록
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        	
        	// 2. JWT 예외 처리 필터 등록
            // JWT 예외 처리 필터를 인증 필터보다 "앞에" 등록
	        // 이렇게 하면 ExceptionFilter가 AuthenticationFilter를 감싸는 형태가 됩니다.
	        .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}