package com.ohgiraffers.emailservice.mailservice;

import java.util.List;

public interface MailService {

    // 1. 회원가입 인증
    void sendSignupVerificationMail(String to, String username);

    // 2. 아이디/비번 찾기
    void sendFindIdMail(String to, List<String> usernames);
    void sendResetPasswordMail(String to, String resetToken);

    // 3. 모임 관련
    void sendClubJoinRequestMail(String hostEmail, String clubName, String applicantName);
    void sendClubJoinApproveMail(String to, String clubName);
    void sendClubJoinRejectMail(String to, String clubName, String reason);
    void sendClubDisbandMail(List<String> memberEmails, String clubName);
    void sendClubMemberLeaveMail(String hostEmail, String clubName, String memberName);
}
