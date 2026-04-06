package com.vibe.realtime.testuser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vibe.realtime.testuser.dto.TestUserResponse;
import com.vibe.realtime.testuser.service.TestUserService;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

@Hidden // 이 컨트롤러의 모든 API가 Swagger UI에서 사라집니다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/test-users")
public class TestUserController {
	
	private final TestUserService testUserService;

    @GetMapping("/{id}")
    public TestUserResponse getTestUser(@PathVariable(value = "id") Integer id) {
        return testUserService.getTestUser(id);
    }
}
