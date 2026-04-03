package com.vibe.realtime.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vibe.realtime.common.response.CommonResponse.ErrorCommonResponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@Target({ElementType.METHOD, ElementType.TYPE}) // 메서드와 클래스 모두 사용 가능
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 정보 유지 (Swagger가 읽기 위함)
@ApiResponses({
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (파라미터 누락, 유효성 위반 등)",
        content = @Content(
    		schema = @Schema(implementation = ErrorCommonResponse.class),
			examples = {
					@ExampleObject(name = "@Valid 검증 실패 시 런타임에 생성된 상세 메시지를 반환합니다.", summary = "유효성 검사 예외 (입력값 오류)", value = "{\"success\":false, \"code\":\"INVALID_INPUT_VALUE\", \"message\":\"잘못된 입력값입니다.\", \"data\":null}")
    		}
        )
    ),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패 (토큰 만료 또는 유효하지 않음)",
		content = @Content(
	        schema = @Schema(implementation = ErrorCommonResponse.class),
	        examples = {
	            @ExampleObject(name = "Token Missing", summary = "토큰 없음", value = "{\"success\":false, \"code\":\"UNAUTHORIZED\", \"message\":\"인증이 필요합니다.\"}"),
	            @ExampleObject(name = "Token Expired", summary = "토큰 만료", value = "{\"success\":false, \"code\":\"EXPIRED_TOKEN\", \"message\":\"만료된 토큰입니다.\"}"),
	            @ExampleObject(name = "Token Invalid", summary = "유효하지 않은 토큰", value = "{\"success\":false, \"code\":\"INVALID_TOKEN\", \"message\":\"유효하지 않은 토큰입니다.\"}")
	        }
	    )
    ),
    @ApiResponse(
        responseCode = "403",
        description = "접근 권한 부족 (인가 실패)",
		content = @Content(
	        schema = @Schema(implementation = ErrorCommonResponse.class),
	        examples = {
	            @ExampleObject(name = "FORBIDDEN", summary = "권한 부족", value = "{\"success\":false, \"code\":\"FORBIDDEN\", \"message\":\"접근 권한이 없습니다.\"}")
	        }
	    )
    ),
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content = @Content(schema = @Schema(implementation = ErrorCommonResponse.class))
    )
})
public @interface ApiCommonResponses {

}
