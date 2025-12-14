package com.ohgiraffers.emailservice.controller;

import com.ohgiraffers.emailservice.dto.*;
import com.ohgiraffers.emailservice.mailservice.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/internal/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/signup-verification")
    public void sendSignupVerification(@RequestBody SignupVerificationMailRequest dto) {
        mailService.sendSignupVerificationMail(dto.username(), dto.nickname());
    }

    @PostMapping("/find-id")
    public void sendFindId(@RequestBody FindIdMailRequest dto) {
        mailService.sendFindIdMail(dto.email(), dto.usernames());
    }

    @PostMapping("/reset-password")
    public void sendResetPassword(@RequestBody ResetPasswordMailRequest dto) {
        mailService.sendResetPasswordMail(dto.email(), dto.resetToken());
    }

    @PostMapping("/club/join-request")
    public void sendClubJoinRequest(@RequestBody ClubJoinRequestMailRequest dto) {
        mailService.sendClubJoinRequestMail(dto.hostEmail(), dto.clubName(), dto.applicantName());
    }

    @PostMapping("/club/join-approve")
    public void sendClubJoinApprove(@RequestBody ClubJoinApproveMailRequest dto) {
        mailService.sendClubJoinApproveMail(dto.email(), dto.clubName());
    }

    @PostMapping("/club/join-reject")
    public void sendClubJoinReject(@RequestBody ClubJoinRejectMailRequest dto) {
        mailService.sendClubJoinRejectMail(dto.email(), dto.clubName(), dto.reason());
    }

    @PostMapping("/club/disband")
    public void sendClubDisband(@RequestBody ClubDisbandMailRequest dto) {
        mailService.sendClubDisbandMail(dto.memberEmails(), dto.clubName());
    }

    @PostMapping("/club/member-leave")
    public void sendClubMemberLeave(@RequestBody ClubMemberLeaveMailRequest dto) {
        mailService.sendClubMemberLeaveMail(dto.hostEmail(), dto.clubName(), dto.memberName());
    }
}
