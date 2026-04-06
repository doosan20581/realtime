package com.vibe.realtime.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vibe.realtime.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target({ElementType.METHOD, ElementType.TYPE}) // 메서드와 클래스 모두 사용 가능
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 정보 유지 (Swagger가 읽기 위함)
@ApiResponses({
	@ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (파라미터 누락, 유효성 위반 등)", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
    @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 만료 또는 유효하지 않음)", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
    @ApiResponse(responseCode = "403", description = "접근 권한 부족 (인가 실패)", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
    @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
})
public @interface ApiCommonResponses {

}
