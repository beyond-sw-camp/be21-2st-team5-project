package com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.repository;

import com.ohgiraffers.readingclubservice.secondbackend.readingclub.entity.ReadingClub;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.entity.ReadingClubReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReadingClubReviewRepository extends JpaRepository<ReadingClubReview, Long> {

    boolean existsByClubIdAndWriterId(ReadingClub clubId, Long writerId);

    Optional<ReadingClubReview> findByReviewIdAndWriterId(Long reviewId, Long writerId);

    // ✅ 어떤 모임(clubId) 안의 리뷰를 최신순으로 15개씩 (Pageable로 페이징)
    Page<ReadingClubReview> findByClubId_IdOrderByCreatedAtDesc(Long clubId, Pageable pageable);

    // ✅ 어떤 모임(clubId) 안의 리뷰를 좋아요 많은 순 + 최신순 보조정렬로
    Page<ReadingClubReview> findByClubId_IdOrderByLikeTotalDescCreatedAtDesc(Long clubId, Pageable pageable);

    Page<ReadingClubReview> findByWriterId_OrderByCreatedAtDesc(Long writerId, Pageable pageable);

}
