package com.ohgiraffers.secondbackend.readingclubreview.service;

import com.ohgiraffers.secondbackend.readingclubreview.dto.response.ReviewLikeToggleResponseDTO;
import com.ohgiraffers.secondbackend.readingclubreview.entity.ReadingClubReview;
import com.ohgiraffers.secondbackend.readingclubreview.entity.ReviewLike;
import com.ohgiraffers.secondbackend.readingclubreview.repository.ReadingClubReviewRepository;
import com.ohgiraffers.secondbackend.readingclubreview.repository.ReviewLikeRepository;
import com.ohgiraffers.secondbackend.user.entity.User;
import com.ohgiraffers.secondbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

    private final UserRepository userRepository;
    private final ReadingClubReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Transactional
    public ReviewLikeToggleResponseDTO toggleLike(Long reviewId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        Long userId = user.getId();
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
    public List<String> getLikedUsernames(Long reviewId, String loginUsername) {

        // 1) 로그인 유저 조회
        User loginUser = userRepository.findByUsername(loginUsername)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        // 2) 리뷰 조회
        ReadingClubReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        // 3) 이 리뷰가 내가 쓴 글인지 검증
        if (!review.getWriterId().equals(loginUser.getId())) {
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
        return userRepository.findAllById(userIds).stream()
                .map(User::getUsername)
                .toList();
    }

}
