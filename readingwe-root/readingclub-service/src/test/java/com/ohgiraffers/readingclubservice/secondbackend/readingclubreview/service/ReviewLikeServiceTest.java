package com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.service;

import com.ohgiraffers.readingclubservice.secondbackend.client.UserFeignClient;
import com.ohgiraffers.readingclubservice.secondbackend.client.dto.UserProfileResponse;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.dto.response.ReviewLikeToggleResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.entity.ReadingClubReview;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.entity.ReviewLike;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.repository.ReadingClubReviewRepository;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.repository.ReviewLikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewLikeServiceTest {

    @Mock
    private ReadingClubReviewRepository reviewRepository;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Mock
    private UserFeignClient userFeignClient;

    @InjectMocks
    private ReviewLikeService reviewLikeService;

    // ================== toggleLike 테스트 ==================

    @Test
    void toggleLike_처음_누르면_좋아요_추가되고_true와_현재_개수를_반환한다() {
        // given
        Long reviewId = 1L;
        Long userId = 100L;

        ReadingClubReview review = ReadingClubReview.builder()
                .reviewTitle("리뷰 제목")
                .reviewContent("리뷰 내용")
                .build();
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        // 아직 좋아요 누른 적 없음
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewLikeRepository.existsByReviewAndUserId(review, userId))
                .thenReturn(false);

        // 좋아요를 저장한 뒤, 총 개수가 1개라고 가정
        when(reviewLikeRepository.countByReview(review))
                .thenReturn(1L);

        // when
        ReviewLikeToggleResponseDTO result =
                reviewLikeService.toggleLike(reviewId, userId);

        // then
        assertTrue(result.isLiked());           // 지금 상태는 liked = true
        assertEquals(1L, result.getLikeTotal());// 총 좋아요 수 1

        verify(reviewLikeRepository, times(1))
                .existsByReviewAndUserId(review, userId);
        verify(reviewLikeRepository, times(1))
                .save(any(ReviewLike.class));
        verify(reviewLikeRepository, times(1))
                .countByReview(review);
    }

    @Test
    void toggleLike_이미_눌렀으면_좋아요_취소되고_false와_현재_개수를_반환한다() {
        // given
        Long reviewId = 1L;
        Long userId = 100L;

        ReadingClubReview review = ReadingClubReview.builder()
                .reviewTitle("리뷰 제목")
                .reviewContent("리뷰 내용")
                .build();
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        // 이미 좋아요 누른 상태
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewLikeRepository.existsByReviewAndUserId(review, userId))
                .thenReturn(true);

        // 취소 후 총 좋아요가 0개라고 가정
        when(reviewLikeRepository.countByReview(review))
                .thenReturn(0L);

        // when
        ReviewLikeToggleResponseDTO result =
                reviewLikeService.toggleLike(reviewId, userId);

        // then
        assertFalse(result.isLiked());          // 지금 상태는 liked = false
        assertEquals(0L, result.getLikeTotal());// 총 좋아요 수 0

        verify(reviewLikeRepository, times(1))
                .deleteByReviewAndUserId(review, userId);
        verify(reviewLikeRepository, times(1))
                .countByReview(review);
    }

    // ================== getLikedUsernames 테스트 ==================

    @Test
    void getLikedUsernames_내가_쓴_리뷰면_좋아요_누른_유저_닉네임_목록을_반환한다() {
        // given
        Long reviewId = 1L;
        Long loginUserId = 10L;   // 로그인한 사용자 (리뷰 작성자)

        // 리뷰 엔티티 생성 + 작성자 ID 설정
        ReadingClubReview review = ReadingClubReview.builder()
                .reviewTitle("리뷰 제목")
                .reviewContent("리뷰 내용")
                .build();
        ReflectionTestUtils.setField(review, "reviewId", reviewId);
        ReflectionTestUtils.setField(review, "writerId", loginUserId);

        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.of(review));

        // 좋아요 엔티티 두 개 (userId 100, 200이 좋아요 누름)
        ReviewLike like1 = ReviewLike.builder()
                .review(review)
                .user(100L)
                .build();
        ReviewLike like2 = ReviewLike.builder()
                .review(review)
                .user(200L)
                .build();

        when(reviewLikeRepository.findByReview_ReviewId(reviewId))
                .thenReturn(List.of(like1, like2));

        // Feign 응답 mock (UserProfileResponse는 mock 으로 충분)
        UserProfileResponse user1 = mock(UserProfileResponse.class);
        UserProfileResponse user2 = mock(UserProfileResponse.class);

        when(userFeignClient.getUserProfileById(100L))
                .thenReturn(user1);
        when(userFeignClient.getUserProfileById(200L))
                .thenReturn(user2);

        when(user1.getNickName()).thenReturn("닉네임1");
        when(user2.getNickName()).thenReturn("닉네임2");

        // when
        List<String> result =
                reviewLikeService.getLikedUsernames(reviewId, loginUserId);

        // then
        assertEquals(2, result.size());
        assertEquals("닉네임1", result.get(0));
        assertEquals("닉네임2", result.get(1));

        verify(reviewRepository, times(1))
                .findById(reviewId);
        verify(reviewLikeRepository, times(1))
                .findByReview_ReviewId(reviewId);
        verify(userFeignClient, times(1))
                .getUserProfileById(100L);
        verify(userFeignClient, times(1))
                .getUserProfileById(200L);
    }

    @Test
    void getLikedUsernames_내가_쓴_리뷰가_아니면_AccessDeniedException_발생() {
        // given
        Long reviewId = 1L;
        Long loginUserId = 10L;    // 지금 로그인한 사람
        Long otherWriterId = 99L;  // 실제 리뷰 작성자

        ReadingClubReview review = ReadingClubReview.builder()
                .reviewTitle("리뷰 제목")
                .reviewContent("리뷰 내용")
                .build();
        ReflectionTestUtils.setField(review, "reviewId", reviewId);
        ReflectionTestUtils.setField(review, "writerId", otherWriterId);

        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.of(review));

        // when & then
        assertThrows(AccessDeniedException.class,
                () -> reviewLikeService.getLikedUsernames(reviewId, loginUserId));

        // 권한 없으므로 아래 리포지토리는 호출되면 안 됨
        verify(reviewLikeRepository, never())
                .findByReview_ReviewId(any());
    }
}
