package com.vibe.realtime.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vibe.realtime.common.response.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // BusinessException 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Object>> handleBusinessException(BusinessException ex) {
    	log.debug("BusinessException error: {}", ex);
    	
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.fail(ex.getErrorCode().getCode(), ex.getMessage()));
    }
  
    // Validation 예외 처리: INVALID_INPUT_VALUE와 상세 메시지 조합
    // @Valid 또는 @Validated 어노테이션을 사용하여 클라이언트로부터 들어온 요청(Request Body)의 데이터가 유효성 검사 규칙을 통과하지 못했을 때 발생합니다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
    	log.debug("BusinessException error: {}", ex);
    	
    	String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        
        // ErrorCode의 코드 + 상세 에러 메세지 출력 형태
        return ResponseEntity
                .badRequest()
                .body(CommonResponse.fail(ErrorCode.INVALID_INPUT_VALUE, errorMessage));
    }

    // 기타 런타임 예외 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse<Object>> handleRuntimeException(RuntimeException ex) {
        log.debug("Unexpected error: {}", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
