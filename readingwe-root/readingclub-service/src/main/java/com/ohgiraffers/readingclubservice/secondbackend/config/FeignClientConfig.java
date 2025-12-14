package com.ohgiraffers.readingclubservice.secondbackend.config;

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

            /* 현재 요청의 Http Servlet Request 를 가져옴 */
            ServletRequestAttributes requestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if(requestAttributes != null) {

                // 2. 내부 user service를 요청하는 상황
                String userId = requestAttributes.getRequest().getHeader("X-User-ID");
                String role = requestAttributes.getRequest().getHeader("X-User-Role");
                String username = requestAttributes.getRequest().getHeader("X-User-Name");
                requestTemplate.header("X-User-ID", userId);
                requestTemplate.header("X-User-Role", role);
                requestTemplate.header("X-User-Name", username);
            }
        };
    }
}