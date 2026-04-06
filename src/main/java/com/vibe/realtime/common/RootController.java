package com.vibe.realtime.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden // 이 컨트롤러의 모든 API가 Swagger UI에서 사라집니다.
@RestController
public class RootController {

    @GetMapping("/")
    public String home() {
        return "Vibe Realtime Project is running! setting test";
    }
}
