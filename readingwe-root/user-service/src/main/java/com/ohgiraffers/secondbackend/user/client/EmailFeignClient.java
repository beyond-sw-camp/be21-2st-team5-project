package com.ohgiraffers.secondbackend.user.client;

import com.ohgiraffers.secondbackend.user.client.dto.SignupVerificationMailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("email-service")
public interface EmailFeignClient {

    @PostMapping("/internal/mail/signup-verification")
    void sendSignupVerificationMail(@RequestBody SignupVerificationMailRequest dto);
}
