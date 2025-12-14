package com.ohgiraffers.emailservice.dto;

public record ClubJoinRequestMailRequest(
        String hostEmail,
        String clubName,
        String applicantName
) {
}
