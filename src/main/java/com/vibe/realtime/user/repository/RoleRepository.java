package com.vibe.realtime.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vibe.realtime.user.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{
	
	// Optional 형태로 Role 객체 반환
    Optional<Role> findByName(String name);
	
}
