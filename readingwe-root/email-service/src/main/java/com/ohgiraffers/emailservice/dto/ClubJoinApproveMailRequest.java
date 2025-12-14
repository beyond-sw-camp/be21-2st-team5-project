package com.ohgiraffers.emailservice.dto;

public record ClubJoinApproveMailRequest (
    String email,
    String clubName
) { }
