package com.ohgiraffers.readingclubservice.secondbackend.client;

import com.ohgiraffers.readingclubservice.secondbackend.client.dto.UserProfileResponse;
import com.ohgiraffers.readingclubservice.secondbackend.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", configuration = FeignClientConfig.class)
public interface UserFeignClient {

    @GetMapping("/user/userId/{userId}")
    UserProfileResponse getUserProfileById(@PathVariable("userId") Long userId);

    @GetMapping("/user/username/{username}")
    UserProfileResponse getUserProfileByUsername(@PathVariable("username") String username);
}
