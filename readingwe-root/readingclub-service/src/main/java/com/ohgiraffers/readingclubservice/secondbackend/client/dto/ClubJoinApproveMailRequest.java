package com.ohgiraffers.readingclubservice.secondbackend.client.dto;

public record ClubJoinApproveMailRequest(
        String email,
        String clubName
) {
}
