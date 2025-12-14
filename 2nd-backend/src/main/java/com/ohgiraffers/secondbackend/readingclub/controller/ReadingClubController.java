package com.ohgiraffers.secondbackend.readingclub.controller;

import com.ohgiraffers.secondbackend.readingclub.dto.request.JoinDecisionDTO;
import com.ohgiraffers.secondbackend.readingclub.dto.request.JoinRequestDTO;
import com.ohgiraffers.secondbackend.readingclub.dto.request.ReadingClubRequestDTO;
import com.ohgiraffers.secondbackend.readingclub.dto.response.JoinResponseDTO;
import com.ohgiraffers.secondbackend.readingclub.dto.response.MyClubResponseDTO;
import com.ohgiraffers.secondbackend.readingclub.dto.response.ReadingClubMemberResponseDTO;
import com.ohgiraffers.secondbackend.readingclub.dto.response.ReadingClubResponseDTO;
import com.ohgiraffers.secondbackend.readingclub.service.ReadingClubService;
import com.ohgiraffers.secondbackend.user.entity.User;
import com.ohgiraffers.secondbackend.user.repository.UserRepository;
import com.ohgiraffers.secondbackend.user.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reading-club")
@RequiredArgsConstructor
public class ReadingClubController {

    private final ReadingClubService readingClubService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping ("/club-create")       // JWT 구현시 @RequestParam 파트 빼고 수정 완료
    public ResponseEntity<ReadingClubResponseDTO> createReadingClub(@RequestBody ReadingClubRequestDTO req
            , Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        long hostId = user.getId();
        ReadingClubResponseDTO res = readingClubService.createReadingClub(req, hostId);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PatchMapping("/update/{clubId}")
    public ResponseEntity<ReadingClubResponseDTO> updateReadingClub(@PathVariable long clubId, @RequestBody ReadingClubRequestDTO req
                                                                     , Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        long hostId = user.getId();

        ReadingClubResponseDTO res = readingClubService.updateReadingClub(clubId, req, hostId);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/delete/{clubId}")
    public ResponseEntity<Void> deleteReadingClub(@PathVariable long clubId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        long hostId = user.getId();

        readingClubService.deleteReadingClub(clubId, hostId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/leave/{clubId}")
    public ResponseEntity<Void> leaveReadingClub(@PathVariable long clubId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        readingClubService.leaveReadingClub(clubId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/join/{clubId}")
    public ResponseEntity<Void> requestJoin(@PathVariable long clubId, @RequestBody(required = false) JoinRequestDTO dto, Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        String message = "";
        if (dto != null && dto.getMessage() != null){
            message = dto.getMessage();
        }

        readingClubService.requestJoin(clubId, user.getId(), message);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/decide/{clubId}/{joinId}")
    public ResponseEntity<Void> decideJoinRequest(@PathVariable Long clubId, @PathVariable Long joinId, @RequestBody JoinDecisionDTO dto, Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        readingClubService.decideJoinRequest(
                clubId,
                user.getId(),
                joinId,
                dto.getStatus()
        );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/join/{joinId}")
    public ResponseEntity<Void> cancelJoin(@PathVariable long joinId, Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        readingClubService.cancleJoin(joinId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/kick/{clubId}/{userId}")
    public ResponseEntity<Void> kickMember(@PathVariable long clubId, @PathVariable long userId, Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));
        readingClubService.kickMember(clubId, user.getId(), userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/join/{clubId}")
    public ResponseEntity<List<JoinResponseDTO>>getJoinRequests(@PathVariable long clubId, Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));
        List<JoinResponseDTO> res = readingClubService.getJoinRequestsForClub(clubId, user.getId());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/join/me")
    public ResponseEntity<List<JoinResponseDTO>> getMyJoinRequests(Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));
        List<JoinResponseDTO> res = readingClubService.getMyJoinRequests(user.getId());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/member/{clubId}")
    public ResponseEntity<List<ReadingClubMemberResponseDTO>> getMembersOfClub(@PathVariable long clubId, Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));
        List<ReadingClubMemberResponseDTO> res = readingClubService.getMembersOfClub(clubId, user.getId());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/my-clubs")
    public ResponseEntity<MyClubResponseDTO> getMyClubs(Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));
        MyClubResponseDTO res = readingClubService.getMyClubs(user.getId());
        return ResponseEntity.ok(res);
    }
}
