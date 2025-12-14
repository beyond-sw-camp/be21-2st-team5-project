package com.ohgiraffers.secondbackend.readingclubreview.controller;

import com.ohgiraffers.secondbackend.readingclubreview.dto.response.ReviewLikeToggleResponseDTO;
import com.ohgiraffers.secondbackend.readingclubreview.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review/like")
@RequiredArgsConstructor
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @PostMapping("/reviewId/{reviewId}")
    public ResponseEntity<ReviewLikeToggleResponseDTO> toggleLike(@PathVariable Long reviewId,
                                                                  Authentication authentication) {
        String username = authentication.getName();

        ReviewLikeToggleResponseDTO response =
                reviewLikeService.toggleLike(reviewId, username);
        return ResponseEntity.ok(response);

    }

    // ✅ 내 게시글에 좋아요 누른 사람 username 조회
    @GetMapping("/{reviewId}/likes")
    public ResponseEntity<List<String>> getLikedUsers(
            @PathVariable Long reviewId,
            Authentication authentication) {

        String username = authentication.getName();

        List<String> likedUsernames =
                reviewLikeService.getLikedUsernames(reviewId, username);

        return ResponseEntity.ok(likedUsernames);
    }
}
