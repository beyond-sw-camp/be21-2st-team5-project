package com.ohgiraffers.emailservice.dto;

public record ClubMemberLeaveMailRequest(
        String hostEmail,
        String clubName,
        String memberName
) {
}
