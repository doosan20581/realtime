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

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final CustomUserDetailsService customUserDetailsService;

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
            .csrf(csrf -> csrf.disable())

            // JWT 기반이므로 세션 사용 안 함
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // URL 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/auth/**", "/ws/**").permitAll()
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

            // JWT 필터 등록
            .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                             UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}