package com.ohgiraffers.bookservice.secondbackend.client;

import com.ohgiraffers.bookservice.secondbackend.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping("/user/userId/{userId}")
    UserProfileResponseDto getUserById(
            @PathVariable("userId") Long userId
    );
}
