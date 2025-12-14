package com.ohgiraffers.secondbackend.readingclubreview.controller;

import com.ohgiraffers.secondbackend.readingclubreview.dto.request.ReadingClubReviewRequestDTO;
import com.ohgiraffers.secondbackend.readingclubreview.dto.response.ReadingClubReviewResponseDTO;
import com.ohgiraffers.secondbackend.readingclubreview.service.ReadingClubReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReadingClubReviewController {

    private final ReadingClubReviewService reviewService;

    @PostMapping("/clubId/{clubId}")
    public ResponseEntity<ReadingClubReviewResponseDTO> createReview(@PathVariable Long clubId, @RequestBody ReadingClubReviewRequestDTO request,
                                                                     Authentication authentication){

        // 로그인한 사용자의 username 가져오기
        String username = authentication.getName();

        ReadingClubReviewResponseDTO response = reviewService.createReview(clubId, request, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/reviewId/{reviewId}")
    public ResponseEntity<ReadingClubReviewResponseDTO> modifyReview(@PathVariable Long reviewId,
                                                                     @RequestBody ReadingClubReviewRequestDTO request,
                                                                     Authentication authentication){

        String username = authentication.getName();

        ReadingClubReviewResponseDTO response = reviewService.modifyReview(reviewId, request, username);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reviewId/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId,
                                             Authentication authentication)
    {
        String username = authentication.getName();

        reviewService.deleteReview(reviewId, username);

        return ResponseEntity.noContent().build();

    }

    /**
     * 특정 모임의 리뷰 목록 조회
     *  - sort=latest (기본값): 최신순
     *  - sort=like        : 좋아요 많은 순
     *  - page=0부터 시작, 한 페이지당 15개
     */
    @GetMapping("/clubId/{clubId}")
    public ResponseEntity<Page<ReadingClubReviewResponseDTO>> getReviews(
            @PathVariable Long clubId,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            Authentication authentication
    ) {
        String username = authentication.getName();

        Page<ReadingClubReviewResponseDTO> result;

        if ("like".equals(sort)) {
            result = reviewService.getReviewsOrderByLike(clubId, page,username);
        } else { // latest 또는 다른 값 들어오면 기본 최신순
            result = reviewService.getReviewsOrderByLatest(clubId, page,username);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ReadingClubReviewResponseDTO>> getMyReviews(@RequestParam(defaultValue = "0") int page, Authentication authentication)
    {
        String username = authentication.getName();
        Page<ReadingClubReviewResponseDTO> result;

        result = reviewService.getMyReviews(username, page);

        return ResponseEntity.ok(result);
    }

}
