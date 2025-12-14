package com.ohgiraffers.readingclubservice.secondbackend.readingclub.repository;

import com.ohgiraffers.readingclubservice.secondbackend.readingclub.entity.JoinRequestStatus;
import com.ohgiraffers.readingclubservice.secondbackend.readingclub.entity.ReadingClubJoin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingClubJoinRepository extends JpaRepository<ReadingClubJoin, Long> {

    // 중복 체크
    boolean existsByClubIdAndUserIdAndStatusIn(Long clubId, Long userId, List<JoinRequestStatus> statuses);

    // 호스트가 신청 목록 조회
    List<ReadingClubJoin> findByClubIdOrderByCreatedAtDesc(Long clubId);

    // 내가 신청한 목록 조회
    List<ReadingClubJoin> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<ReadingClubJoin> findByIdAndClubId(Long id, Long clubId);
}
