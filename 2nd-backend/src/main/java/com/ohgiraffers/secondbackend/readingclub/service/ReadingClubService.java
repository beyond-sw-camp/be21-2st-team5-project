package com.ohgiraffers.secondbackend.readingclub.service;

import com.ohgiraffers.secondbackend.readingclub.dto.request.ReadingClubRequestDTO;
import com.ohgiraffers.secondbackend.readingclub.dto.response.JoinResponseDTO;
import com.ohgiraffers.secondbackend.readingclub.dto.response.MyClubResponseDTO;
import com.ohgiraffers.secondbackend.readingclub.dto.response.ReadingClubMemberResponseDTO;
import com.ohgiraffers.secondbackend.readingclub.dto.response.ReadingClubResponseDTO;
import com.ohgiraffers.secondbackend.readingclub.entity.*;
import com.ohgiraffers.secondbackend.readingclub.repository.ReadingClubJoinRepository;
import com.ohgiraffers.secondbackend.readingclub.repository.ReadingClubMemberRepository;
import com.ohgiraffers.secondbackend.readingclub.repository.ReadingClubRepository;
import com.ohgiraffers.secondbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReadingClubService {

    private final ReadingClubRepository readingClubRepository;
    private final ReadingClubMemberRepository readingClubMemberRepository;
    private final ReadingClubJoinRepository readingClubJoinRepository;
    private final UserRepository userRepository;

    private ReadingClubResponseDTO convert(ReadingClub club) {
        return ReadingClubResponseDTO.builder()
                .id(club.getId())
                .name(club.getName())
                .description(club.getDescription())
                .status(club.getStatus())
                .createdAt(club.getCreatedAt())
                .hostUserId(club.getUserId())
                .categoryId(club.getCategoryId())
                .build();
    }

    private JoinResponseDTO convertJoin(ReadingClubJoin join){
        return JoinResponseDTO.builder()
                .id(join.getId())
                .clubId(join.getClubId())
                .userId(join.getUserId())
                .message(join.getMessage())
                .status(join.getStatus())
                .createdAt(join.getCreatedAt())
                .build();
    }
    private ReadingClubMemberResponseDTO convertMember(ReadingClubMember member) {
        return ReadingClubMemberResponseDTO.builder()
                .id(member.getId())
                .clubId(member.getClubId())
                .userId(member.getUserId())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }


    @Transactional      // 모임 생성
    public ReadingClubResponseDTO createReadingClub(ReadingClubRequestDTO req, long hostId) {
        ReadingClub rc = ReadingClub.builder()
                .name(req.getName())
                .description(req.getDescription())
                .userId(hostId)
                .categoryId(req.getCategoryId())
                .status(ReadingClubStatus.OPEN)
                .build();

        ReadingClub saved = readingClubRepository.save(rc);
        return convert(saved);
    }

    @Transactional      // 모임 수정
    public ReadingClubResponseDTO updateReadingClub(Long clubId, ReadingClubRequestDTO req, long hostId) {
        ReadingClub club = readingClubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));

        if (club.getUserId() != hostId) {
            throw new SecurityException("모임을 수정할 권한이 없습니다.");
        }

        club.update(req.getName(), req.getDescription(), req.getCategoryId());
        return convert(club);
    }

    @Transactional      // 모임 수정
    public void deleteReadingClub(Long clubId, long hostId){
        ReadingClub club = readingClubRepository.findById(clubId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 모임입니다."));

        if (club.getUserId() != hostId){
            throw new SecurityException("모임을 삭제할 권한이 없습니다.");
        }
        club.finish();
    }

    @Transactional
    public void leaveReadingClub(Long clubId, long userId){
        ReadingClub club = readingClubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));

        if (club.getUserId() == userId){
            throw new IllegalStateException("모임장은 탈퇴할 수 없습니다. 삭제(해산)만 가능합니다.");
        }
        ReadingClubMember member = readingClubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임에 가입되어 있지 않습니다."));
        if (member.getRole() == ReadingClubMemberRole.LEFT){
            throw new IllegalStateException("이미 탈퇴한 멤버입니다.");
        }
        club.removeMember();

        member.changeRole(ReadingClubMemberRole.LEFT);
    }

    @Transactional
    public void requestJoin(Long clubId, Long userId, String message){
        ReadingClub club = readingClubRepository.findById(clubId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));

        if(readingClubMemberRepository.existsByClubIdAndUserId(clubId, userId)){
            throw new IllegalStateException("이미 해당 모임에 가입했습니다.");
        }

        boolean existRequest = readingClubJoinRepository.existsByClubIdAndUserIdAndStatusIn(clubId, userId, List.of(JoinRequestStatus.PENDING));

        if(existRequest){
            throw new IllegalStateException("이미 신청했습니다.");
        }

        if (club.getStatus() != ReadingClubStatus.OPEN) {
            throw new IllegalStateException("현재 신청이 불가능한 모임입니다.");
        }

        ReadingClubJoin request = ReadingClubJoin.builder()
                .clubId(clubId)
                .userId(userId)
                .message(message)
                .status(JoinRequestStatus.PENDING)
                .build();

        readingClubJoinRepository.save(request);
    }

    @Transactional
    public void decideJoinRequest(Long clubId, Long hostId, Long joinId, JoinRequestStatus status){
        if(status == JoinRequestStatus.PENDING){
            throw new IllegalStateException("결정은 APPROVED 또는 REJECTED만 가능");
        }

        ReadingClub club = readingClubRepository.findById(clubId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));

        if(club.getUserId() != hostId){
            throw new SecurityException("모임장만 신청을 승인/거절할 수 있습니다.");
        }

        ReadingClubJoin request = readingClubJoinRepository.findByIdAndClubId(joinId, clubId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 신청입니다.")
        );

        if(request.getStatus() != JoinRequestStatus.PENDING){
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }

        request.setStatus(status);

        if(status == JoinRequestStatus.APPROVED){
            club.addMember();
            if(!readingClubMemberRepository.existsByClubIdAndUserId(clubId, request.getUserId())){
                ReadingClubMember member = ReadingClubMember.builder()
                        .clubId(clubId)
                        .userId(request.getUserId())
                        .role(ReadingClubMemberRole.MEMBER)
                        .build();

                readingClubMemberRepository.save(member);
            }
        }
    }

    @Transactional
    public void cancleJoin(long joinId, long userId){
        ReadingClubJoin req = readingClubJoinRepository.findById(joinId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 신청입니다.")
        );
        if(req.getUserId() != userId){
            throw new SecurityException("본인 신청만 취소할 수 있습니다.");
        }
        if(req.getStatus() != JoinRequestStatus.PENDING){
            throw new IllegalStateException("이미 처리된 신청입니다. 클럽 탈퇴를 신청해주세요");
        }

        readingClubJoinRepository.delete(req);
    }

    @Transactional
    public void kickMember(long clubId, long hostId, long targetId){
        ReadingClub club = readingClubRepository.findById(clubId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 모임입니다.")
        );
        if(club.getUserId() != hostId){
            throw new SecurityException("모임장만 회원을 강퇴할 수 있습니다.");
        }
        if(club.getUserId() == targetId){
            throw new IllegalStateException("모임장은 강퇴할 수 없습니다.");
        }

        ReadingClubMember member = readingClubMemberRepository.findByClubIdAndUserId(clubId, targetId).orElseThrow(
                () -> new IllegalArgumentException("해당 모임의 멤버가 아닙니다.")
        );
        if(member.getRole() == ReadingClubMemberRole.LEFT){
            throw new IllegalStateException("이미 탈퇴/강퇴된 멤버입니다.");
        }
        club.removeMember();
        member.changeRole(ReadingClubMemberRole.LEFT);
    }

    @Transactional(readOnly = true)
    public List<JoinResponseDTO> getJoinRequestsForClub(long clubId, long hostId){
        ReadingClub club = readingClubRepository.findById(clubId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));
        if(club.getUserId() != hostId){
            throw new SecurityException("모임장만 신청 목록을 조회할 수 있습니다.");
        }
        return readingClubJoinRepository.findByClubIdOrderByCreatedAtDesc(clubId).stream().map(this::convertJoin).toList();
    }

    @Transactional
    public List<JoinResponseDTO> getMyJoinRequests(long userId){
        return readingClubJoinRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::convertJoin).toList();
    }

    @Transactional(readOnly = true)
    public List<ReadingClubMemberResponseDTO> getMembersOfClub(Long clubId, long userId){
        ReadingClub club = readingClubRepository.findById(clubId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 모임입니다.")
        );
        ReadingClubMember member = readingClubMemberRepository.findByClubIdAndUserId(clubId, userId).orElseThrow(
                () -> new SecurityException("해당 모임의 멤버가 아닙니다.")
        );
        if(member.getRole() == ReadingClubMemberRole.LEFT){
            throw new SecurityException("탈퇴 혹은 강퇴 당한 멤버는 조회할 수 없습니다.");
        }
        return readingClubMemberRepository.findByClubId(clubId).stream().filter(mem -> member.getRole() != ReadingClubMemberRole.LEFT)
                .map(this::convertMember).toList();
    }

    @Transactional
    public MyClubResponseDTO getMyClubs(long userId){
        List<ReadingClubResponseDTO> hosted = readingClubRepository.findByUserId(userId).stream().map(this::convert).toList();

        List<ReadingClubMember> members = readingClubMemberRepository.findByUserIdAndRoleNot(userId, ReadingClubMemberRole.LEFT);
        List<ReadingClubResponseDTO> joined = members.stream()
                .map(m -> readingClubRepository.findById(m.getClubId()).orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 모임입니다."))).map(this::convert).toList();
        return MyClubResponseDTO.builder()
                .hostedClubs(hosted)
                .joinedClubs(joined)
                .build();
    }
}
