package com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.controller;

import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.dto.request.ReadingClubReviewRequestDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.dto.response.ReadingClubReviewResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.service.ReadingClubReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review API", description = "모임 리뷰 등록, 수정, 삭제, 조회 API")
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReadingClubReviewController {

    private final ReadingClubReviewService reviewService;

    @PostMapping("/clubId/{clubId}")
    public ResponseEntity<ReadingClubReviewResponseDTO> createReview(@PathVariable Long clubId, @RequestBody ReadingClubReviewRequestDTO request,
                                                                     HttpServletRequest req){

        // 로그인한 사용자의 username 가져오기
        String id = req.getHeader("X-User-ID");
        Long userId = Long.valueOf(id);

        ReadingClubReviewResponseDTO response = reviewService.createReview(clubId, request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/reviewId/{reviewId}")
    public ResponseEntity<ReadingClubReviewResponseDTO> modifyReview(@PathVariable Long reviewId,
                                                                     @RequestBody ReadingClubReviewRequestDTO request,
                                                                     HttpServletRequest req){

        String id = req.getHeader("X-User-ID");
        Long userId = Long.valueOf(id);

        ReadingClubReviewResponseDTO response = reviewService.modifyReview(reviewId, request, userId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reviewId/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId,
                                             HttpServletRequest req)
    {
        String id = req.getHeader("X-User-ID");
        Long userId = Long.valueOf(id);

        reviewService.deleteReview(reviewId, userId);

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
            HttpServletRequest req
    ) {
        String id = req.getHeader("X-User-ID");
        Long userId = Long.valueOf(id);

        Page<ReadingClubReviewResponseDTO> result;

        if ("like".equals(sort)) {
            result = reviewService.getReviewsOrderByLike(clubId, page,userId);
        } else { // latest 또는 다른 값 들어오면 기본 최신순
            result = reviewService.getReviewsOrderByLatest(clubId, page,userId);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ReadingClubReviewResponseDTO>> getMyReviews(@RequestParam(defaultValue = "0") int page,HttpServletRequest req)
    {
        String id = req.getHeader("X-User-ID");
        Long userId = Long.valueOf(id);

        Page<ReadingClubReviewResponseDTO> result;

        result = reviewService.getMyReviews(userId, page);

        return ResponseEntity.ok(result);
    }

}
