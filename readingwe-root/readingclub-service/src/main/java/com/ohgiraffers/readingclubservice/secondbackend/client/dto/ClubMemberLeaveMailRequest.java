package com.ohgiraffers.readingclubservice.secondbackend.client.dto;

public record ClubMemberLeaveMailRequest(
        String hostname,
        String clubName,
        String memberName
) {
}
