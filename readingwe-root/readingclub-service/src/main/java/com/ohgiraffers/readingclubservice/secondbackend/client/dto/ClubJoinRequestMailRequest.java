package com.ohgiraffers.readingclubservice.secondbackend.client.dto;

import java.util.List;

public record ClubJoinRequestMailRequest(
        String hostEmail,
        String clubName,
        String applicantName
) {
}
