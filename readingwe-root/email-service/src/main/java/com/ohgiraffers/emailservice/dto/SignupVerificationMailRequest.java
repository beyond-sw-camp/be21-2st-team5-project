package com.ohgiraffers.emailservice.dto;

public record SignupVerificationMailRequest(
        String username,
        String nickname
) {}
