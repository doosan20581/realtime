package com.vibe.realtime.testuser.repository;

// assertJ 라이브러리의 검증 문법 사용
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vibe.realtime.testuser.entity.TestUser;

/*
 * JPA Repository 테스트 전용 어노테이션
 *
 * 특징
 * 1. JPA 관련 Bean만 로딩 (EntityManager, Repository 등)
 * 2. Service, Controller 등은 로딩하지 않음
 * 3. 기본적으로 In-memory DB(H2)를 사용
 * 4. 테스트 종료 후 자동 Rollback
 *
 * → 실제 DB에 영향을 주지 않는 안전한 테스트 환경
 */
@DataJpaTest
class TestUserRepositoryTest {

    /*
     * Spring이 TestUserRepository Bean을 자동 주입
     *
     * 실제 구현체는 Spring Data JPA가
     * 런타임에 Proxy 객체로 생성
     */
    @Autowired
    TestUserRepository testUserRepository;

    /*
     * JUnit5 테스트 메서드
     *
     * 목적:
     * Repository의 save() 와 findById() 정상 동작 확인
     */
    @Test
    void findUser() {

        /*
         * 테스트용 사용자 엔티티 생성
         *
         * builder()는 Lombok의 @Builder 기능
         */
        TestUser user = TestUser.builder()
                .username("kim")
                .email("kim@test.com")
                .password("1234")
                .build();

        /*
         * JPA save()
         *
         * 내부 동작
         * EntityManager.persist()
         *
         * 실행 결과
         * → DB에 insert
         * → user.id 값 자동 생성
         */
        testUserRepository.save(user);

        /*
         * save 이후 생성된 id로 사용자 조회
         *
         * 반환 타입 Optional
         * → null 방지
         */
        Optional<TestUser> result = testUserRepository.findById(user.getId());

        /*
         * 검증
         *
         * result가 존재하는지 확인
         *
         * assertThat(result).isPresent()
         * = Optional 값이 존재해야 테스트 통과
         */
        assertThat(result).isPresent();
    }
}