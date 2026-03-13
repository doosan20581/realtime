package com.vibe.realtime.testuser.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.vibe.realtime.testuser.dto.TestUserResponse;
import com.vibe.realtime.testuser.service.TestUserService;

@WebMvcTest(TestUserController.class)
class TestUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestUserService testUserService;

    @Test
    void getTestUser() throws Exception {

        TestUserResponse response =
                TestUserResponse.builder()
                        .id(1)
                        .username("kim")
                        .email("kim@test.com")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(testUserService.getTestUser(1))
                .thenReturn(response);

        mockMvc.perform(get("/api/test-users/1"))
        		.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("kim"));
    }
}