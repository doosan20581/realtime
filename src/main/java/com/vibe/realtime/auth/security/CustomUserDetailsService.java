package com.vibe.realtime.auth.security;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vibe.realtime.user.entity.User;
import com.vibe.realtime.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 로그인 시 호출되는 핵심 메서드
     *
     * @param email
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	
    	// 로그에는 상세 정보를 남겨서 개발자가 추적할 수 있게 함
        log.debug("Trying to authenticate user: {}", email);

        // 1. DB에서 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("이메일 또는 비밀번호가 올바르지 않습니다.");
    			}); 
        		// 보안을 위해 "사용자를 찾을 수 없습니다" 대신 통합 메시지 사용
        		// 여기서 던지는 예외는 서비스 단으로 이동
        
        // 2. 권한 변환
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        // 3. UserDetails 객체로 변환
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                authorities
        );
    }
}