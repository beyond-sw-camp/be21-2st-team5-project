package com.ohgiraffers.emailservice.dto;

public record ClubJoinRejectMailRequest(
        String email,
        String clubName,
        String reason
) {
}
