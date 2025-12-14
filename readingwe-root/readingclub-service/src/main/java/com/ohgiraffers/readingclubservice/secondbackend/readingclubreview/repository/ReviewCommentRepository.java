package com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.repository;

import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

    // reviewCommentId + user 기준으로 찾기
    Optional<ReviewComment> findByReviewCommentIdAndUser(Long reviewCommentId, Long user);

    // 해당 리뷰에 달린 댓글을 작성 시각 기준으로 최신순 정렬
    List<ReviewComment> findByReview_ReviewIdOrderByCreatedAtDesc(Long reviewId);
}
