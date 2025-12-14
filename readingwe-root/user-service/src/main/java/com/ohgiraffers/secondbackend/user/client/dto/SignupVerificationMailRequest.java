package com.ohgiraffers.secondbackend.user.client.dto;

public record SignupVerificationMailRequest(
        String username,
        String nickname
) {
}
