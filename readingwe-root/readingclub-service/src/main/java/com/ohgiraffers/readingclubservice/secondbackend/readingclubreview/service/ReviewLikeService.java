package com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.service;

import com.ohgiraffers.readingclubservice.secondbackend.client.UserFeignClient;
import com.ohgiraffers.readingclubservice.secondbackend.client.dto.UserProfileResponse;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.dto.response.ReviewLikeToggleResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.entity.ReadingClubReview;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.entity.ReviewLike;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.repository.ReadingClubReviewRepository;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.repository.ReviewLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

    private final ReadingClubReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserFeignClient userFeignClient;

    @Transactional
    public ReviewLikeToggleResponseDTO toggleLike(Long reviewId, Long userId) {

        ReadingClubReview review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        boolean alreadyLiked = reviewLikeRepository.existsByReviewAndUserId(review, userId);

        boolean nowLiked;
        if (alreadyLiked) {
            // 좋아요 취소
            reviewLikeRepository.deleteByReviewAndUserId(review, userId);
            review.decreaseLike();
            nowLiked = false;
        } else {
            // 좋아요 추가
            ReviewLike like = ReviewLike.builder()
                    .review(review)
                    .user(userId)
                    .build();
            reviewLikeRepository.save(like);
            review.increaseLike();
            nowLiked = true;
        }
        long likeCount = reviewLikeRepository.countByReview(review);
        return new ReviewLikeToggleResponseDTO(nowLiked, likeCount);
    }

    @Transactional(readOnly = true)
    public List<String> getLikedUsernames(Long reviewId, Long loginUserId) {


        // 2) 리뷰 조회
        ReadingClubReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        // 3) 이 리뷰가 내가 쓴 글인지 검증
        if (!review.getWriterId().equals(loginUserId)) {
            throw new AccessDeniedException("자신이 작성한 후기글의 좋아요만 조회할 수 있습니다.");
        }

        // 4) 해당 리뷰에 달린 좋아요 전체 조회
        List<ReviewLike> likes = reviewLikeRepository.findByReview_ReviewId(reviewId);

        // 5) userId 리스트로 변환 (중복 제거)
        List<Long> userIds = likes.stream()
                .map(ReviewLike::getUserId)
                .distinct()
                .toList();

        if (userIds.isEmpty()) {
            return List.of();   // 빈 리스트 반환
        }

        // 6) userId로 User 조회 → username 리스트 뽑기

        return userIds.stream()
                .map(userFeignClient::getUserProfileById) // user-service 호출
                .map(UserProfileResponse::getNickName)                     // nickname만 추출
                .toList();
    }

}
