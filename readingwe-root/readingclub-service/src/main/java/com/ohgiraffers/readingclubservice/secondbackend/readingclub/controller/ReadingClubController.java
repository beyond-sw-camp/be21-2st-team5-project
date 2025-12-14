package com.ohgiraffers.readingclubservice.secondbackend.readingclub.controller;

import com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.request.JoinDecisionDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.request.JoinRequestDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.request.ReadingClubRequestDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.response.JoinResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.response.MyClubResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.response.ReadingClubMemberResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.response.ReadingClubResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.service.ReadingClubService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ReadingClub API", description = "모임 생성, 수정, 삭제, 조회, 참가 신청 관리 API")
@RestController
@RequestMapping("/reading-club")
@RequiredArgsConstructor
public class ReadingClubController {

    private final ReadingClubService readingClubService;

    private long getCurrentUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-ID");
        if (userIdHeader == null) {
            throw new IllegalArgumentException("X-User-ID 헤더가 없습니다.");
        }
        return Long.parseLong(userIdHeader);
    }

    private String getCurrentUsername(HttpServletRequest request) {
        return request.getHeader("X-User-Name");   // 없어도 null 허용
    }

    private String getCurrentUserRole(HttpServletRequest request) {
        return request.getHeader("X-User-Role");   // 지금은 안 쓰지만 일단 남겨둠
    }

    @PostMapping ("/club-create")       // JWT 구현시 @RequestParam 파트 빼고 수정 완료
    public ResponseEntity<ReadingClubResponseDTO> createReadingClub(
            @RequestBody ReadingClubRequestDTO req,
            HttpServletRequest request
    ) {
        long hostId = getCurrentUserId(request);

        ReadingClubResponseDTO res = readingClubService.createReadingClub(req, hostId);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PatchMapping("/update/{clubId}")
    public ResponseEntity<ReadingClubResponseDTO> updateReadingClub(
            @PathVariable long clubId,
            @RequestBody ReadingClubRequestDTO req,
            HttpServletRequest request
    ) {
        long hostId = getCurrentUserId(request);

        ReadingClubResponseDTO res = readingClubService.updateReadingClub(clubId, req, hostId);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/delete/{clubId}")
    public ResponseEntity<Void> deleteReadingClub(
            @PathVariable long clubId,
            HttpServletRequest request
    ) {
        long hostId = getCurrentUserId(request);

        readingClubService.deleteReadingClub(clubId, hostId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/leave/{clubId}")
    public ResponseEntity<Void> leaveReadingClub(
            @PathVariable long clubId,
            HttpServletRequest request
    ) {
        long userId = getCurrentUserId(request);

        readingClubService.leaveReadingClub(clubId, userId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/join/{clubId}")
    public ResponseEntity<Void> requestJoin(
            @PathVariable long clubId,
            @RequestBody(required = false) JoinRequestDTO dto,
            HttpServletRequest request
    ) {
        long userId = getCurrentUserId(request);

        String message = "";
        if (dto != null && dto.getMessage() != null) {
            message = dto.getMessage();
        }

        readingClubService.requestJoin(clubId, userId, message);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/decide/{clubId}/{joinId}")
    public ResponseEntity<Void> decideJoinRequest(
            @PathVariable Long clubId,
            @PathVariable Long joinId,
            @RequestBody JoinDecisionDTO dto,
            HttpServletRequest request
    ) {
        long hostId = getCurrentUserId(request);

        readingClubService.decideJoinRequest(
                clubId,
                hostId,
                joinId,
                dto.getStatus()
        );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/join/{joinId}")
    public ResponseEntity<Void> cancelJoin(
            @PathVariable long joinId,
            HttpServletRequest request
    ) {
        long userId = getCurrentUserId(request);

        readingClubService.cancleJoin(joinId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/join/{clubId}")
    public ResponseEntity<List<JoinResponseDTO>> getJoinRequests(
            @PathVariable long clubId,
            HttpServletRequest request
    ) {
        long hostId = getCurrentUserId(request);

        List<JoinResponseDTO> res = readingClubService.getJoinRequestsForClub(clubId, hostId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/join/me")
    public ResponseEntity<List<JoinResponseDTO>> getMyJoinRequests(
            HttpServletRequest request
    ) {
        long userId = getCurrentUserId(request);

        List<JoinResponseDTO> res = readingClubService.getMyJoinRequests(userId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/member/{clubId}")
    public ResponseEntity<List<ReadingClubMemberResponseDTO>> getMembersOfClub(
            @PathVariable long clubId,
            HttpServletRequest request
    ) {
        long userId = getCurrentUserId(request);

        List<ReadingClubMemberResponseDTO> res = readingClubService.getMembersOfClub(clubId, userId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/my-clubs")
        public ResponseEntity<MyClubResponseDTO> getMyClubs(
                HttpServletRequest request
        ) {
            long userId = getCurrentUserId(request);

            MyClubResponseDTO res = readingClubService.getMyClubs(userId);
            return ResponseEntity.ok(res);
        }
}
