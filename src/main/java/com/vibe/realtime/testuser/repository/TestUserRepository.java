package com.vibe.realtime.testuser.repository;

// Spring Data JPA에서 제공하는 기본 Repository 인터페이스
// CRUD 기능이 이미 구현되어 있음
import org.springframework.data.jpa.repository.JpaRepository;

import com.vibe.realtime.testuser.entity.TestUser;

// Repository 역할
// DB 접근 계층 (DAO 역할)
//
// JpaRepository<Entity 타입, PK 타입>
//
// 여기서는
// Entity : TestUser
// PK 타입 : Integer
//
// 즉 "test_users 테이블을 관리하는 Repository" 의미
public interface TestUserRepository extends JpaRepository<TestUser, Integer> {

    // 아무 코드도 없어도 됨
    // 이유: JpaRepository가 이미 다음 메서드를 제공함

    /*
        save()
        findById()
        findAll()
        deleteById()
        count()
        existsById()
     */

}