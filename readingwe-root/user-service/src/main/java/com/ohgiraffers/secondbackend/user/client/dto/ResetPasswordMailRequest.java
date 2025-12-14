package com.ohgiraffers.secondbackend.user.client.dto;

public record ResetPasswordMailRequest(
        String email,
        java.util.List<String> usernames
) {
}
