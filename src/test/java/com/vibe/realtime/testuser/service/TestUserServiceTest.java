package com.vibe.realtime.testuser.service;

// Assertions와 Mockito 기능 import
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vibe.realtime.testuser.dto.TestUserResponse;
import com.vibe.realtime.testuser.entity.TestUser;
import com.vibe.realtime.testuser.repository.TestUserRepository;

// Mockito 확장을 JUnit 5에 연결
// → @Mock, @InjectMocks 등 Mockito 기능 활성화
@ExtendWith(MockitoExtension.class)
class TestUserServiceTest {

    // TestUserRepository를 Mock 객체로 생성
    // 실제 DB에 접근하지 않고 Mockito가 대신 반환값을 제공
    @Mock
    private TestUserRepository testUserRepository;

    // TestUserService 객체를 생성하고,
    // Mock으로 생성된 testUserRepository를 의존성으로 주입
    @InjectMocks
    private TestUserService testUserService;

    // 테스트 메서드 정의
    @Test
    void getTestUser_success() {

        // 테스트용 더미 사용자 객체 생성
        TestUser user = TestUser.builder()
                .id(1)
                .username("kim")
                .email("kim@test.com")
                .password("1234")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Mockito 동작 등록
        // testUserRepository.findById(1) 호출 시
        // Optional.of(user)를 반환하도록 지정
        // 실제 DB에는 접근하지 않음
        when(testUserRepository.findById(1))
                .thenReturn(Optional.of(user));

        // 서비스 메서드 호출
        // 내부적으로 testUserRepository.findById(1)을 호출하지만,
        // 위에서 등록한 Mock 반환값을 사용
        TestUserResponse response = testUserService.getTestUser(1);

        // 검증(assertion)
        // 서비스가 반환한 DTO의 username이 "kim"인지 확인
        assertThat(response.getUsername()).isEqualTo("kim");
    }
}