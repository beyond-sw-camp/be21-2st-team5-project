package com.ohgiraffers.emailservice.dto;

import java.util.List;

public record FindIdMailRequest(
        String email,
        List<String> usernames
) {
}
