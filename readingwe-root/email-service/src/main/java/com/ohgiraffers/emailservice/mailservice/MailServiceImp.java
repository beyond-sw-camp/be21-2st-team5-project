package com.ohgiraffers.emailservice.mailservice;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailServiceImp implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MailProperties mailProperties;

    private String getFromAddress() {
        // 따로 mail.from 을 두고 싶으면 application.yml 에 추가한 뒤 그걸 주입해도 됨
        return mailProperties.getUsername();
    }

    private void sendHtmlMail(String to, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        String html = templateEngine.process(templateName, context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                            StandardCharsets.UTF_8.name());

            helper.setFrom(getFromAddress());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("메일 전송 중 오류가 발생했습니다.", e);
        }
    }

    private void sendHtmlMailToMany(List<String> toList, String subject, String templateName, Map<String, Object> variables) {
        for (String to : toList) {
            sendHtmlMail(to, subject, templateName, variables);
        }
    }

    // 1. 회원가입 인증 메일
    @Override
    public void sendSignupVerificationMail(String to, String username) {
        String subject = "[readwe] 회원가입을 환영합니다!";

        Map<String, Object> vars = new HashMap<>();
        vars.put("username", username);

        // templates/mail/SignupVerification.html
        sendHtmlMail(to, subject, "mail/SignupVerification", vars);
    }

    // 2. 아이디 찾기 메일
    @Override
    public void sendFindIdMail(String to, List<String> usernames) {
        String subject = "[서비스명] 아이디 찾기 안내";

        Map<String, Object> vars = new HashMap<>();
        vars.put("usernames", usernames);

        // templates/mail/FindId.html
        sendHtmlMail(to, subject, "mail/FindId", vars);
    }

    // 2-2. 비밀번호 재설정 메일
    @Override
    public void sendResetPasswordMail(String to, String resetToken) {
        String subject = "[서비스명] 비밀번호 재설정 안내";

        Map<String, Object> vars = new HashMap<>();
        vars.put("resetToken", resetToken);

        // templates/mail/ResetPassword.html
        sendHtmlMail(to, subject, "mail/ResetPassword", vars);
    }

    // 3-1. 모임 가입 신청 메일 (호스트에게 발송)
    @Override
    public void sendClubJoinRequestMail(String hostEmail, String clubName, String applicantName) {
        String subject = "[서비스명] 모임 가입 신청 알림";

        Map<String, Object> vars = new HashMap<>();
        vars.put("clubName", clubName);
        vars.put("applicantName", applicantName);

        // templates/mail/ClubJoinRequest.html
        sendHtmlMail(hostEmail, subject, "mail/ClubJoinRequest", vars);
    }

    // 3-2. 모임 가입 승인 메일
    @Override
    public void sendClubJoinApproveMail(String to, String clubName) {
        String subject = "[서비스명] 모임 가입 승인 안내";

        Map<String, Object> vars = new HashMap<>();
        vars.put("clubName", clubName);

        // templates/mail/ClubJoinApprove.html
        sendHtmlMail(to, subject, "mail/ClubJoinApprove", vars);
    }

    // 3-3. 모임 가입 거절 메일
    @Override
    public void sendClubJoinRejectMail(String to, String clubName, String reason) {
        String subject = "[서비스명] 모임 가입 거절 안내";

        Map<String, Object> vars = new HashMap<>();
        vars.put("clubName", clubName);
        vars.put("reason", reason);

        // 현재 파일명이 ClubJoinJeject.html 로 되어 있음 (오타)
        // 파일명을 ClubJoinReject.html 로 바꾸면 여기 문자열도 같이 바꿔라.
        sendHtmlMail(to, subject, "mail/ClubJoinJeject", vars);
    }

    // 3-4. 모임 해산 안내 메일 (모든 멤버에게 발송)
    @Override
    public void sendClubDisbandMail(List<String> memberEmails, String clubName) {
        String subject = "[서비스명] 모임 해산 안내";

        Map<String, Object> vars = new HashMap<>();
        vars.put("clubName", clubName);

        // templates/mail/ClubDisband.html
        sendHtmlMailToMany(memberEmails, subject, "mail/ClubDisband", vars);
    }

    // 3-5. 모임원 탈퇴 알림 메일 (호스트에게 발송)
    @Override
    public void sendClubMemberLeaveMail(String hostEmail, String clubName, String memberName) {
        String subject = "[서비스명] 모임 탈퇴 알림";

        Map<String, Object> vars = new HashMap<>();
        vars.put("clubName", clubName);
        vars.put("memberName", memberName);

        // templates/mail/ClubLeave.html
        sendHtmlMail(hostEmail, subject, "mail/ClubLeave", vars);
    }
}
