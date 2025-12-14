package com.ohgiraffers.bookservice.secondbackend.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // 현재 요청의 HttpServletRequest 가져오기
            ServletRequestAttributes requestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (requestAttributes != null) {
                var request = requestAttributes.getRequest();

                // 기존 X-User-* 헤더
                String userId = request.getHeader("X-User-ID");
                String role = request.getHeader("X-User-Role");
                String username = request.getHeader("X-User-Name");

                if (userId != null) requestTemplate.header("X-User-ID", userId);
                if (role != null) requestTemplate.header("X-User-Role", role);
                if (username != null) requestTemplate.header("X-User-Name", username);

                // JWT Authorization 헤더
                String token = request.getHeader("Authorization");
                if (token != null) {
                    requestTemplate.header("Authorization", token);
                }
            }
        };
    }
}
