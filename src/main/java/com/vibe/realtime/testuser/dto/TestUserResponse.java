package com.vibe.realtime.testuser.dto;

// 날짜/시간 타입
import java.time.LocalDateTime;

// Entity 클래스 (DB 테이블과 매핑되는 객체)
import com.vibe.realtime.testuser.entity.TestUser;

// Lombok 기능
import lombok.Builder;
import lombok.Getter;

// Getter 자동 생성
// getId(), getUsername() 등 자동 생성
@Getter

// Builder 패턴 생성
// 객체 생성 시 builder() 사용 가능
@Builder
public class TestUserResponse {

    // API 응답에 포함될 필드들
	private Integer id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity -> DTO 변환 메서드
    // Service에서 Entity를 DTO로 변환할 때 사용
    public static TestUserResponse from(TestUser user) {

        // Builder 패턴을 사용하여 DTO 객체 생성
        return TestUserResponse.builder()

                // Entity 값 → DTO 값 복사
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())

                // 객체 생성 완료
                .build();
    }
}