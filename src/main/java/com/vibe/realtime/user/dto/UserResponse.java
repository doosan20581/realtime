package com.vibe.realtime.user.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.vibe.realtime.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "UserResponse", description = "사용자 정보 Response DTO")
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Integer id;

    @Schema(description = "사용자 이름", example = "tester")
    private String name;

    @Schema(description = "사용자 이메일", example = "tester@example.com")
    private String email;

    @Schema(description = "사용자 권한 목록", example = "[\"ROLE_USER\"]")
    private List<String> roles;
    
    // -----------------------------
    // User 엔티티 → UserResponse 변환
    // -----------------------------
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles() != null
                        ? user.getRoles().stream()
                              .map(role -> role.getName())
                              .collect(Collectors.toList())
                        : List.of())
                .build();
    }
    
}