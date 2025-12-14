package com.ohgiraffers.secondbackend.readingclubreview.repository;

import com.ohgiraffers.secondbackend.readingclubreview.entity.ReadingClubReview;
import com.ohgiraffers.secondbackend.readingclubreview.entity.ReviewLike;
import com.ohgiraffers.secondbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

   //  리뷰에 이 유저가 좋아요를 눌렀는지?
    boolean existsByReviewAndUserId(ReadingClubReview review, Long user);

    // 이 리뷰에 이 유저가 누른 좋아요 삭제
    void deleteByReviewAndUserId(ReadingClubReview review, Long user);

    // 특정 리뷰에 달린 모든 좋아요
    List<ReviewLike> findByReview_ReviewId(Long reviewId);

    // (선택) 이 리뷰의 좋아요 개수
    long countByReview(ReadingClubReview review);
}
