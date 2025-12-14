package com.ohgiraffers.bookservice.secondbackend.config;

import com.ohgiraffers.bookservice.secondbackend.book.util.HeaderAuthenticationFilter;
import com.ohgiraffers.bookservice.secondbackend.book.util.RestAccessDeniedHandler;
import com.ohgiraffers.bookservice.secondbackend.book.util.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(restAuthenticationEntryPoint)
                                .accessDeniedHandler(restAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // 🔥 ADMIN 체크는 Controller가 직접 처리하므로 Security에서는 막지 않음
                        .requestMatchers("/book/save", "/book/delete/**").permitAll()

                        // 🔥 유저 관련은 전체 허용 (Gateway에서 role 전달)
                        .requestMatchers("/book/**", "/booklike/**", "/book-report/**",
                                "/book-report-comment/**", "/book-report-like/**").permitAll()

                        // Swagger
                        .requestMatchers("/swagger-ui.html","/swagger-ui/**",
                                "/v3/api-docs/**","/swagger-resources/**").permitAll()

                        // 로그인/회원가입
                        .requestMatchers("/auth/**","/users","/internal/mail/**").permitAll()

                        // 나머지만 인증 필요
                        .anyRequest().authenticated()
                )

                .addFilterBefore(headerAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public HeaderAuthenticationFilter headerAuthenticationFilter() {
        return new HeaderAuthenticationFilter();
    }

}
