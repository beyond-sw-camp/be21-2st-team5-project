package com.ohgiraffers.readingclubservice.secondbackend.readingclub.service;

import com.ohgiraffers.readingclubservice.secondbackend.client.EmailFeignClient;
import com.ohgiraffers.readingclubservice.secondbackend.client.UserFeignClient;
import com.ohgiraffers.readingclubservice.secondbackend.client.dto.ClubDisbandMailRequest;
import com.ohgiraffers.readingclubservice.secondbackend.client.dto.UserProfileResponse;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.request.ReadingClubRequestDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.response.JoinResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.response.MyClubResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.response.ReadingClubMemberResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.response.ReadingClubResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.entity.*;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.repository.ReadingClubJoinRepository;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.repository.ReadingClubMemberRepository;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.repository.ReadingClubRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingClubServiceTest {

    @Mock
    ReadingClubRepository readingClubRepository;

    @Mock
    ReadingClubMemberRepository readingClubMemberRepository;

    @Mock
    ReadingClubJoinRepository readingClubJoinRepository;

    @Mock
    EmailFeignClient emailFeignClient;

    @Mock
    UserFeignClient userFeignClient;

    @InjectMocks
    ReadingClubService readingClubService;

    @DisplayName("모임 생성 성공")
    @Test
    void createReadingClub_success() {
        // given
        long hostId = 1L;
        ReadingClubRequestDTO req = new ReadingClubRequestDTO(
                "독서 모임",
                "설명입니다",
                10L
        );

        ReadingClub saved = ReadingClub.builder()
                .name(req.getName())
                .description(req.getDescription())
                .userId(hostId)
                .categoryId(req.getCategoryId())
                .status(ReadingClubStatus.OPEN)
                .build();

        when(readingClubRepository.save(any(ReadingClub.class)))
                .thenReturn(saved);

        // when
        ReadingClubResponseDTO result = readingClubService.createReadingClub(req, hostId);

        // then
        assertNotNull(result);
        assertEquals(req.getName(), result.getName());
        assertEquals(hostId, result.getHostUserId());
        assertEquals(ReadingClubStatus.OPEN, result.getStatus());

        verify(readingClubRepository, times(1)).save(any(ReadingClub.class));
    }

    @DisplayName("모임 삭제 시 멤버 + 호스트에게 해산 메일 발송")
    @Test
    void deleteReadingClub_success() {
        // given
        long clubId = 10L;
        long hostId = 1L;

        ReadingClub club = ReadingClub.builder()
                .name("테스트 모임")
                .userId(hostId)
                .status(ReadingClubStatus.OPEN)
                .build();

        ReadingClubMember member1 = ReadingClubMember.builder()
                .id(100L)
                .clubId(clubId)
                .userId(2L)
                .role(ReadingClubMemberRole.MEMBER)
                .build();

        ReadingClubMember member2 = ReadingClubMember.builder()
                .id(101L)
                .clubId(clubId)
                .userId(3L)
                .role(ReadingClubMemberRole.LEFT)  // 메일 대상 아님
                .build();

        when(readingClubRepository.findById(clubId))
                .thenReturn(Optional.of(club));

        when(readingClubMemberRepository.findByClubId(clubId))
                .thenReturn(List.of(member1, member2));

        when(userFeignClient.getUserProfileById(1L))
                .thenReturn(new UserProfileResponse(1L, "호스트닉", "host@example.com", "USER"));
        when(userFeignClient.getUserProfileById(2L))
                .thenReturn(new UserProfileResponse(2L, "멤버닉", "member@example.com", "USER"));


        // when
        readingClubService.deleteReadingClub(clubId, hostId);

        // then
        assertEquals(ReadingClubStatus.FINISHED, club.getStatus());

        ArgumentCaptor<ClubDisbandMailRequest> mailCaptor =
                ArgumentCaptor.forClass(ClubDisbandMailRequest.class);
        verify(emailFeignClient, times(1)).sendClubDisband(mailCaptor.capture());

        ClubDisbandMailRequest mailReq = mailCaptor.getValue();
        assertTrue(mailReq.memberEmails().contains("host@example.com"));
        assertTrue(mailReq.memberEmails().contains("member@example.com"));
        assertEquals("테스트 모임", mailReq.clubName());
    }

    @DisplayName("모임 가입 신청 성공")
    @Test
    void requestJoin_success() {
        // given
        long clubId = 10L;
        long userId = 2L;
        String message = "가입하고 싶어요";

        ReadingClub club = ReadingClub.builder()
                .name("테스트 모임")
                .userId(1L)
                .status(ReadingClubStatus.OPEN)
                .build();

        when(readingClubRepository.findById(clubId))
                .thenReturn(Optional.of(club));

        when(readingClubMemberRepository.existsByClubIdAndUserId(clubId, userId))
                .thenReturn(false);

        when(readingClubJoinRepository.existsByClubIdAndUserIdAndStatusIn(
                eq(clubId), eq(userId), anyList()))
                .thenReturn(false);

        when(userFeignClient.getUserProfileById(1L))
                .thenReturn(new UserProfileResponse(1L, "host@example.com", "호스트닉", "USER"));
        when(userFeignClient.getUserProfileById(2L))
                .thenReturn(new UserProfileResponse(2L, "member@example.com", "신청자닉", "USER"));

        // when
        readingClubService.requestJoin(clubId, userId, message);

        // then
        ArgumentCaptor<ReadingClubJoin> joinCaptor =
                ArgumentCaptor.forClass(ReadingClubJoin.class);
        verify(readingClubJoinRepository, times(1)).save(joinCaptor.capture());
        ReadingClubJoin savedJoin = joinCaptor.getValue();

        assertEquals(clubId, savedJoin.getClubId());
        assertEquals(userId, savedJoin.getUserId());
        assertEquals(message, savedJoin.getMessage());
        assertEquals(JoinRequestStatus.PENDING, savedJoin.getStatus());

        verify(emailFeignClient, times(1)).sendClubJoinRequest(any());
    }

    @DisplayName("가입 신청 승인 시 멤버 추가")
    @Test
    void decideJoinRequest_approve_success() {
        long clubId = 10L;
        long hostId = 1L;
        long joinId = 100L;
        long applicantId = 2L;

        ReadingClub club = ReadingClub.builder()
                .name("테스트 모임")
                .userId(hostId)
                .status(ReadingClubStatus.OPEN)
                .build();

        ReadingClubJoin join = ReadingClubJoin.builder()
                .id(joinId)
                .clubId(clubId)
                .userId(applicantId)
                .message("가입요청")
                .status(JoinRequestStatus.PENDING)
                .build();

        when(readingClubRepository.findById(clubId))
                .thenReturn(Optional.of(club));
        when(readingClubJoinRepository.findByIdAndClubId(joinId, clubId))
                .thenReturn(Optional.of(join));

        when(readingClubMemberRepository.existsByClubIdAndUserId(clubId, applicantId))
                .thenReturn(false);

        when(userFeignClient.getUserProfileById(applicantId))
                .thenReturn(new UserProfileResponse(applicantId, "applicant@example.com", "신청자닉", "USER"));

        // when
        readingClubService.decideJoinRequest(
                clubId, hostId, joinId, JoinRequestStatus.APPROVED);

        // then
        assertEquals(JoinRequestStatus.APPROVED, join.getStatus());

        verify(readingClubMemberRepository, times(1)).save(any(ReadingClubMember.class));
        verify(emailFeignClient, times(1)).sendClubJoinApprove(any());
    }

    @DisplayName("가입 신청 취소 - 본인이 아니면 SecurityException")
    @Test
    void cancleJoin_notOwner_throwsSecurityException() {
        long joinId = 100L;
        long ownerId = 1L;
        long otherId = 2L;

        ReadingClubJoin join = ReadingClubJoin.builder()
                .id(joinId)
                .userId(ownerId)
                .build();

        when(readingClubJoinRepository.findById(joinId))
                .thenReturn(Optional.of(join));

        assertThrows(SecurityException.class,
                () -> readingClubService.cancleJoin(joinId, otherId));

        verify(readingClubJoinRepository, never()).delete(any());
    }

    @DisplayName("모임 탈퇴 성공 시 멤버 상태 LEFT 변경 및 메일 발송")
    @Test
    void leaveReadingClub_success() {
        // given
        long clubId = 10L;
        long hostId = 1L;
        long memberId = 2L;

        ReadingClub club = ReadingClub.builder()
                .name("테스트 모임")
                .description("설명")
                .userId(hostId)
                .categoryId(10L)
                .status(ReadingClubStatus.OPEN)
                .build();

        ReflectionTestUtils.setField(club, "currentMember", 1);

        ReadingClubMember member = ReadingClubMember.builder()
                .id(100L)
                .clubId(clubId)
                .userId(memberId)
                .role(ReadingClubMemberRole.MEMBER)
                .build();

        when(readingClubRepository.findById(clubId))
                .thenReturn(Optional.of(club));
        when(readingClubMemberRepository.findByClubIdAndUserId(clubId, memberId))
                .thenReturn(Optional.of(member));

        // host / 멤버 프로필
        when(userFeignClient.getUserProfileById(hostId))
                .thenReturn(new UserProfileResponse(hostId, "호스트닉", "host@example.com", "USER"));
        when(userFeignClient.getUserProfileById(memberId))
                .thenReturn(new UserProfileResponse(memberId, "멤버닉", "member@example.com", "USER"));

        // when
        readingClubService.leaveReadingClub(clubId, memberId);

        // then
        assertEquals(ReadingClubMemberRole.LEFT, member.getRole());
        verify(emailFeignClient, times(1)).sendClubMemberLeave(any());
    }

    @DisplayName("모임장은 leaveReadingClub 호출 시 IllegalStateException")
    @Test
    void leaveReadingClub_hostCannotLeave() {
        // given
        long clubId = 10L;
        long hostId = 1L;

        ReadingClub club = ReadingClub.builder()
                .name("테스트 모임")
                .userId(hostId)
                .categoryId(10L)
                .status(ReadingClubStatus.OPEN)
                .build();

        when(readingClubRepository.findById(clubId))
                .thenReturn(Optional.of(club));

        // when & then
        assertThrows(IllegalStateException.class,
                () -> readingClubService.leaveReadingClub(clubId, hostId));

        verify(readingClubMemberRepository, never()).findByClubIdAndUserId(anyLong(), anyLong());
        verify(emailFeignClient, never()).sendClubMemberLeave(any());
    }

    @DisplayName("호스트가 모임 신청 목록 조회 성공")
    @Test
    void getJoinRequestsForClub_success() {
        // given
        long clubId = 10L;
        long hostId = 1L;

        ReadingClub club = ReadingClub.builder()
                .name("테스트 모임")
                .userId(hostId)
                .categoryId(10L)
                .status(ReadingClubStatus.OPEN)
                .build();

        ReadingClubJoin join1 = ReadingClubJoin.builder()
                .id(100L)
                .clubId(clubId)
                .userId(2L)
                .message("가입 요청 1")
                .status(JoinRequestStatus.PENDING)
                .build();

        ReadingClubJoin join2 = ReadingClubJoin.builder()
                .id(101L)
                .clubId(clubId)
                .userId(3L)
                .message("가입 요청 2")
                .status(JoinRequestStatus.APPROVED)
                .build();

        when(readingClubRepository.findById(clubId))
                .thenReturn(Optional.of(club));
        when(readingClubJoinRepository.findByClubIdOrderByCreatedAtDesc(clubId))
                .thenReturn(List.of(join2, join1));   // 정렬된 상태라고 가정

        // when
        List<JoinResponseDTO> result =
                readingClubService.getJoinRequestsForClub(clubId, hostId);

        // then
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getId());
        assertEquals(clubId, result.get(0).getClubId());
        assertEquals(3L, result.get(0).getUserId());
    }

    @DisplayName("호스트가 아닌 사용자가 신청 목록 조회 시 SecurityException")
    @Test
    void getJoinRequestsForClub_notHost_throwsSecurityException() {
        // given
        long clubId = 10L;
        long hostId = 1L;
        long otherUserId = 2L;

        ReadingClub club = ReadingClub.builder()
                .name("테스트 모임")
                .userId(hostId)
                .categoryId(10L)
                .status(ReadingClubStatus.OPEN)
                .build();

        when(readingClubRepository.findById(clubId))
                .thenReturn(Optional.of(club));

        // when & then
        assertThrows(SecurityException.class,
                () -> readingClubService.getJoinRequestsForClub(clubId, otherUserId));
    }

    @DisplayName("내가 신청한 모임 신청 목록 조회 성공")
    @Test
    void getMyJoinRequests_success() {
        // given
        long userId = 2L;

        ReadingClubJoin join1 = ReadingClubJoin.builder()
                .id(100L)
                .clubId(10L)
                .userId(userId)
                .message("가입 요청 1")
                .status(JoinRequestStatus.PENDING)
                .build();

        ReadingClubJoin join2 = ReadingClubJoin.builder()
                .id(101L)
                .clubId(11L)
                .userId(userId)
                .message("가입 요청 2")
                .status(JoinRequestStatus.APPROVED)
                .build();

        when(readingClubJoinRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(join2, join1));

        // when
        List<JoinResponseDTO> result =
                readingClubService.getMyJoinRequests(userId);

        // then
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getId());
        assertEquals(11L, result.get(0).getClubId());
        assertEquals(userId, result.get(0).getUserId());
    }


    @DisplayName("모임 멤버 조회 성공 - LEFT 멤버는 제외")
    @Test
    void getMembersOfClub_success() {
        // given
        long clubId = 10L;
        long requesterId = 2L;  // 멤버로 가입한 사용자

        ReadingClub club = ReadingClub.builder()
                .name("테스트 모임")
                .userId(1L)
                .categoryId(10L)
                .status(ReadingClubStatus.OPEN)
                .build();

        // 요청자 본인 (MEMBER)
        ReadingClubMember requesterMember = ReadingClubMember.builder()
                .id(100L)
                .clubId(clubId)
                .userId(requesterId)
                .role(ReadingClubMemberRole.MEMBER)
                .build();

        // 멤버 2명 (USER 2, 3), LEFT 1명 (USER 4)
        ReadingClubMember m1 = requesterMember;
        ReadingClubMember m2 = ReadingClubMember.builder()
                .id(101L)
                .clubId(clubId)
                .userId(3L)
                .role(ReadingClubMemberRole.MEMBER)
                .build();
        ReadingClubMember left = ReadingClubMember.builder()
                .id(102L)
                .clubId(clubId)
                .userId(4L)
                .role(ReadingClubMemberRole.LEFT)
                .build();

        when(readingClubRepository.findById(clubId))
                .thenReturn(Optional.of(club));
        when(readingClubMemberRepository.findByClubIdAndUserId(clubId, requesterId))
                .thenReturn(Optional.of(requesterMember));

        when(readingClubMemberRepository.findByClubId(clubId))
                .thenReturn(List.of(m1, m2, left));

        // when
        List<ReadingClubMemberResponseDTO> result =
                readingClubService.getMembersOfClub(clubId, requesterId);

        // then
        assertEquals(2, result.size());
        List<Long> resultUserIds = result.stream()
                .map(ReadingClubMemberResponseDTO::getUserId)
                .toList();

        assertTrue(resultUserIds.contains(2L));
        assertTrue(resultUserIds.contains(3L));
        assertFalse(resultUserIds.contains(4L));
    }

    @DisplayName("모임 멤버가 아닌 사용자가 멤버 목록 조회 시 SecurityException")
    @Test
    void getMembersOfClub_notMember_throwsSecurityException() {
        // given
        long clubId = 10L;
        long userId = 2L;

        ReadingClub club = ReadingClub.builder()
                .name("테스트 모임")
                .userId(1L)
                .categoryId(10L)
                .status(ReadingClubStatus.OPEN)
                .build();

        when(readingClubRepository.findById(clubId))
                .thenReturn(Optional.of(club));
        when(readingClubMemberRepository.findByClubIdAndUserId(clubId, userId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(SecurityException.class,
                () -> readingClubService.getMembersOfClub(clubId, userId));
    }

    @DisplayName("내가 만든 모임 + 내가 가입한 모임 조회 성공")
    @Test
    void getMyClubs_success() {
        // given
        long userId = 2L;

        // 내가 호스트인 모임 2개
        ReadingClub hosted1 = ReadingClub.builder()
                .name("내가 만든 모임 1")
                .userId(userId)
                .categoryId(1L)
                .status(ReadingClubStatus.OPEN)
                .build();

        ReadingClub hosted2 = ReadingClub.builder()
                .name("내가 만든 모임 2")
                .userId(userId)
                .categoryId(2L)
                .status(ReadingClubStatus.OPEN)
                .build();

        when(readingClubRepository.findByUserId(userId))
                .thenReturn(List.of(hosted1, hosted2));

        // 내가 멤버(호스트는 아닌)로 가입한 모임들
        ReadingClubMember joinedMember1 = ReadingClubMember.builder()
                .clubId(20L)
                .userId(userId)
                .role(ReadingClubMemberRole.MEMBER)
                .build();

        ReadingClubMember joinedMember2 = ReadingClubMember.builder()
                .clubId(21L)
                .userId(userId)
                .role(ReadingClubMemberRole.MEMBER)
                .build();

        when(readingClubMemberRepository.findByUserIdAndRoleNot(
                userId, ReadingClubMemberRole.LEFT))
                .thenReturn(List.of(joinedMember1, joinedMember2));

        // 가입한 모임들의 실제 club 정보
        ReadingClub joinedClub1 = ReadingClub.builder()
                .name("가입한 모임 1")
                .userId(99L)
                .categoryId(3L)
                .status(ReadingClubStatus.OPEN)
                .build();

        ReadingClub joinedClub2 = ReadingClub.builder()
                .name("가입한 모임 2")
                .userId(100L)
                .categoryId(4L)
                .status(ReadingClubStatus.OPEN)
                .build();

        when(readingClubRepository.findById(20L))
                .thenReturn(Optional.of(joinedClub1));
        when(readingClubRepository.findById(21L))
                .thenReturn(Optional.of(joinedClub2));

        // when
        MyClubResponseDTO result = readingClubService.getMyClubs(userId);

        // then
        assertNotNull(result);
        assertEquals(2, result.getHostedClubs().size());
        assertEquals(2, result.getJoinedClubs().size());

        List<String> hostedNames = result.getHostedClubs().stream()
                .map(ReadingClubResponseDTO::getName)
                .toList();

        List<String> joinedNames = result.getJoinedClubs().stream()
                .map(ReadingClubResponseDTO::getName)
                .toList();

        assertTrue(hostedNames.containsAll(
                List.of("내가 만든 모임 1", "내가 만든 모임 2")));

        assertTrue(joinedNames.containsAll(
                List.of("가입한 모임 1", "가입한 모임 2")));
    }


}
