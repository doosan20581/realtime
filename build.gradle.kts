plugins {
	java
	//id("org.springframework.boot") version "4.0.1"
	id("org.springframework.boot") version "3.3.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.vibe"
version = "0.0.1-SNAPSHOT"
description = "Vibe Cording project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	/*
		implementation - 애플리케이션 코드에서 필요
		runtimeOnly - 런타임에서만 필요 (예: JWT 구현체, H2)
		compileOnly + annotationProcessor - 코드 컴파일/빌드 시만 필요 (Lombok)
		testImplementation - 테스트 코드에서만 필요
	*/

    // Spring Web MVC, REST API, Controller, Tomcat 내장
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Spring Data JPA + Hibernate, DB 연동
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    
    // WebSocket 지원, 실시간 통신
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    
    // MySQL JDBC 드라이버, DB 연결용
    implementation("com.mysql:mysql-connector-j")
    
    // Bean Validation (javax.validation), @Valid 등 사용
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Spring Security, 인증/인가 처리
    implementation("org.springframework.boot:spring-boot-starter-security")
    
    // JWT 토큰 생성/검증용 API
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    
    // OpenAPI / Swagger UI, API 문서 자동 생성
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    
    // Spring Data Redis, RedisTemplate, Repository 사용 가능
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Lettuce Redis 클라이언트, 비동기/동기 지원
    implementation("io.lettuce:lettuce-core:6.2.2.RELEASE") 
    
    // JWT 구현체, 런타임 시 필요
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    
    // JWT와 Jackson 연동 (JSON 직렬화/역직렬화)
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    
    // Lombok, 코드 단축 (Getter/Setter, Builder 등)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    // Lombok 테스트용 어노테이션 처리
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    
    // Spring Boot 테스트용, JUnit, Mockito 포함
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    
    // JUnit 플랫폼 런처, 테스트 실행 시 필요
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    
    // H2 임베디드 DB (RDS 연동 전 테스트용)
    // runtimeOnly("com.h2database:h2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
