package com.vibe.realtime.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SignupRequest", description = "회원가입 요청 DTO")
public class SignupRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @Schema(description = "사용자 이메일", example = "tester@example.com", required = true)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다.")
    @Schema(description = "비밀번호", example = "123456", required = true)
    private String password;

    @NotBlank(message = "사용자 이름은 필수입니다.")
    @Size(max = 50, message = "사용자 이름은 최대 50자까지 가능합니다.")
    @Schema(description = "사용자 이름", example = "tester", required = true)
    private String username;
    
}