package com.ohgiraffers.emailservice.dto;

public record ResetPasswordMailRequest(
        String email,
        String resetToken
) {
}
