package com.vibe.realtime.testuser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vibe.realtime.testuser.dto.TestUserResponse;
import com.vibe.realtime.testuser.service.TestUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test-users")
public class TestUserController {
	
	private final TestUserService testUserService;

    @GetMapping("/{id}")
    public TestUserResponse getTestUser(@PathVariable(value = "id") Integer id) {
        return testUserService.getTestUser(id);
    }
}
