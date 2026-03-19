package com.vibe.realtime.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vibe.realtime.user.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	// Optional 형태로 User 객체 반환
    Optional<User> findByEmail(String email);

    // 이메일 존재 여부 체크
    boolean existsByEmail(String email);
    
}
