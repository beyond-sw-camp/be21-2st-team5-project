package com.ohgiraffers.secondbackend.readingclubreview.controller;

import com.ohgiraffers.secondbackend.readingclubreview.dto.request.ReviewCommentRequestDTO;
import com.ohgiraffers.secondbackend.readingclubreview.dto.response.ReviewCommentResponseDTO;
import com.ohgiraffers.secondbackend.readingclubreview.service.ReviewCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/review/comment")
@RequiredArgsConstructor
public class ReviewCommentController {
    private final ReviewCommentService reviewCommentService;

    @PostMapping("/reviewId/{reviewId}")
    public ResponseEntity<ReviewCommentResponseDTO> createReviewComments(@PathVariable("reviewId") Long reviewId,
                                                                 @RequestBody ReviewCommentRequestDTO request,
                                                                 Authentication authentication) {
        // 로그인한 사용자의 username 가져오기
        String username = authentication.getName();

        ReviewCommentResponseDTO response = reviewCommentService.createReviewComment(reviewId, request,username);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/commentId/{commentId}")
    public ResponseEntity<ReviewCommentResponseDTO> modifyComments(@PathVariable("commentId") Long commentId,
                                                                   @RequestBody ReviewCommentRequestDTO request,
                                                                   Authentication  authentication) {
        String username = authentication.getName();

        ReviewCommentResponseDTO response = reviewCommentService.modifyComment(commentId, request, username);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/commentId/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId,
                                              Authentication authentication) {
        String username = authentication.getName();

        reviewCommentService.deleteComment(commentId, username);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reviewId/{reviewId}")
    public ResponseEntity<List<ReviewCommentResponseDTO>> viewComment(@PathVariable("reviewId") Long reviewId,
                                                                Authentication authentication) {
        String username = authentication.getName();

        List<ReviewCommentResponseDTO> comments = reviewCommentService.viewComment(reviewId, username);

        return ResponseEntity.ok(comments);
    }
}
