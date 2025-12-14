package com.ohgiraffers.user.config;

import com.ohgiraffers.secondbackend.user.filter.JWTAuthenticationFilter;
import com.ohgiraffers.secondbackend.user.service.UserService;
import com.ohgiraffers.secondbackend.user.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        JWTAuthenticationFilter jwtAuthenticationFilter=
                new JWTAuthenticationFilter(jwtUtil,userService,redisTemplate);



        /*URL 추가해서 권한 부여하면 됩니다!*/
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/auth/signup","/auth/login","/reading-club/**", "/book-report/**", "book-report-comment/**").permitAll()
                        .requestMatchers("/booklike/**","/userlike/**").hasAuthority("USER")
                        .requestMatchers("/user/**","/book/**").hasAnyAuthority("USER","ADMIN")


                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
