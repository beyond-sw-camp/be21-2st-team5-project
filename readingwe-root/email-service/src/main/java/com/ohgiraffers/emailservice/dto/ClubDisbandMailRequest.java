package com.ohgiraffers.emailservice.dto;

import java.util.List;

public record ClubDisbandMailRequest(
        List<String> memberEmails,
        String clubName
) {
}
