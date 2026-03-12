package com.vibe.realtime.testuser.service;

// Spring이 이 클래스를 "Service Bean"으로 등록
// 비즈니스 로직을 담당하는 계층
import org.springframework.stereotype.Service;

import com.vibe.realtime.testuser.dto.TestUserResponse;
import com.vibe.realtime.testuser.entity.TestUser;
import com.vibe.realtime.testuser.repository.TestUserRepository;

import lombok.RequiredArgsConstructor;

// Spring Bean 등록
@Service

// Lombok 기능
// final 필드를 자동으로 생성자 주입
@RequiredArgsConstructor
public class TestUserService {

    // Repository 의존성 주입
    // DB 접근을 담당하는 객체
	private final TestUserRepository testUserRepository;

    // 사용자 1명 조회 서비스
    public TestUserResponse getTestUser(Integer id) {

        // Repository를 통해 DB에서 사용자 조회
        // Optional<TestUser> 형태로 반환됨
        TestUser user = testUserRepository.findById(id)

                // 만약 데이터가 없으면 예외 발생
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 사용자가 없습니다. id=" + id)
                );

        // Entity -> DTO 변환
        // API 응답용 객체 생성
        return TestUserResponse.from(user);
    }
}