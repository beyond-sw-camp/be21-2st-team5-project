package com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.controller;

import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.dto.response.ReviewLikeToggleResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.service.ReviewLikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Review Like API", description = "모임 리뷰 좋아요 추가, 삭제, 조회 API")
@RestController
@RequestMapping("/review/like")
@RequiredArgsConstructor
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @PostMapping("/reviewId/{reviewId}")
    public ResponseEntity<ReviewLikeToggleResponseDTO> toggleLike(@PathVariable Long reviewId,
                                                                  HttpServletRequest req) {
        String id = req.getHeader("X-User-ID");
        Long userId = Long.valueOf(id);

        ReviewLikeToggleResponseDTO response =
                reviewLikeService.toggleLike(reviewId, userId);
        return ResponseEntity.ok(response);

    }

    // ✅ 내 게시글에 좋아요 누른 사람 username 조회
    @GetMapping("/{reviewId}/likes")
    public ResponseEntity<List<String>> getLikedUsers(
            @PathVariable Long reviewId,
            HttpServletRequest req) {

        String id = req.getHeader("X-User-ID");
        Long userId = Long.valueOf(id);

        List<String> likedUsernames =
                reviewLikeService.getLikedUsernames(reviewId, userId);

        return ResponseEntity.ok(likedUsernames);
    }
}
