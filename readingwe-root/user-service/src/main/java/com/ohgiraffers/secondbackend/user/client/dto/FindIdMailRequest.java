package com.ohgiraffers.secondbackend.user.client.dto;

public record FindIdMailRequest(
        String email,
        String username,
        String verificationCode
) {
}
