package com.vibe.realtime.user.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vibe.realtime.common.exception.BusinessException;
import com.vibe.realtime.common.exception.ErrorCode;
import com.vibe.realtime.user.entity.Role;
import com.vibe.realtime.user.entity.User;
import com.vibe.realtime.user.repository.RoleRepository;
import com.vibe.realtime.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // final로 선언, 생성자 주입
    private final RoleRepository roleRepository;
    
    @Transactional
    public User createUser(String email, String password, String name) {

        // 1️. 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2️. 비밀번호 암호화 + User 엔티티 생성
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();
        
        // 2.1. 기본 ROLE_USER 할당
        Role roleUser = roleRepository
        		.findByName("ROLE_USER")
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
        user.getRoles().add(roleUser);

        // 3️. DB 저장 후 반환
        try {
        	return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }
    
}
