package com.vibe.realtime.common.util;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {

    // 1. 실제 클라이언트 IP 추출 (Proxy 환경 고려)
    public static String getClientIp(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For", 
            "Proxy-Client-IP", 
            "WL-Proxy-Client-IP", 
            "HTTP_CLIENT_IP", 
            "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For의 경우 여러 IP가 콤마로 나열될 수 있어 첫 번째 IP를 선택
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }

    // 2. 기기 정보(User-Agent) 추출
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}