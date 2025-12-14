package com.ohgiraffers.secondbackend.readingclub.repository;

import com.ohgiraffers.secondbackend.readingclub.entity.ReadingClubMember;
import com.ohgiraffers.secondbackend.readingclub.entity.ReadingClubMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingClubMemberRepository extends JpaRepository<ReadingClubMember, Long> {
    boolean existsByClubIdAndUserId(Long clubId, Long userId);
    List<ReadingClubMember> findByClubId(Long clubId);
    List<ReadingClubMember> findByUserIdAndRoleNot(Long userId, ReadingClubMemberRole role);
    Optional<ReadingClubMember> findByClubIdAndUserId(Long clubId, Long userId);
}
