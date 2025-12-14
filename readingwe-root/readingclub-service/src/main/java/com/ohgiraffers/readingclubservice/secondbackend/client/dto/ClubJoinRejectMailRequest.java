package com.ohgiraffers.readingclubservice.secondbackend.client.dto;

public record ClubJoinRejectMailRequest(
        String email,
        String clubName,
        String reason
) {
}
