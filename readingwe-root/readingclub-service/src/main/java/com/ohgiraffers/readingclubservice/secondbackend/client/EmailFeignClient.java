package com.ohgiraffers.readingclubservice.secondbackend.client;

import com.ohgiraffers.readingclubservice.secondbackend.client.dto.*;
import com.ohgiraffers.readingclubservice.secondbackend.client.dto.*;
import com.ohgiraffers.readingclubservice.secondbackend.config.FeignClientConfig;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.request.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "email-service", configuration = FeignClientConfig.class)
public interface EmailFeignClient {

    @PostMapping("/internal/mail/club/join-request")
    void sendClubJoinRequest(@RequestBody ClubJoinRequestMailRequest dto);

    @PostMapping("/internal/mail/club/join-approve")
    void sendClubJoinApprove(@RequestBody ClubJoinApproveMailRequest dto);

    @PostMapping("/internal/mail/club/join-reject")
    void sendClubJoinReject(@RequestBody ClubJoinRejectMailRequest dto);

    @PostMapping("/internal/mail/club/disband")
    void sendClubDisband(@RequestBody ClubDisbandMailRequest dto);

    @PostMapping("/internal/mail/club/member-leave")
    void sendClubMemberLeave(@RequestBody ClubMemberLeaveMailRequest dto);
}
