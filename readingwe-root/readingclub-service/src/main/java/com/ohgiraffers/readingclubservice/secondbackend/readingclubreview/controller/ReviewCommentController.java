package com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.controller;

import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.dto.request.ReviewCommentRequestDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.dto.response.ReviewCommentResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.service.ReviewCommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Review Comment API", description = "모임 리뷰 댓글 작성, 수정, 삭제, 조회 API")
@RestController
@RequestMapping("/review/comment")
@RequiredArgsConstructor
public class ReviewCommentController {
    private final ReviewCommentService reviewCommentService;

    @PostMapping("/reviewId/{reviewId}")
    public ResponseEntity<ReviewCommentResponseDTO> createReviewComments(@PathVariable("reviewId") Long reviewId,
                                                                         @RequestBody ReviewCommentRequestDTO request,
                                                                         HttpServletRequest req) {
        // 로그인한 사용자의 username 가져오기
        String id = req.getHeader("X-User-ID");
        Long userId = Long.valueOf(id);

        ReviewCommentResponseDTO response = reviewCommentService.createReviewComment(reviewId, request,userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/commentId/{commentId}")
    public ResponseEntity<ReviewCommentResponseDTO> modifyComments(@PathVariable("commentId") Long commentId,
                                                                   @RequestBody ReviewCommentRequestDTO request,
                                                                   HttpServletRequest req) {
        // 로그인한 사용자의 username 가져오기
        String id = req.getHeader("X-User-ID");
        Long userId = Long.valueOf(id);

        ReviewCommentResponseDTO response = reviewCommentService.modifyComment(commentId, request, userId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/commentId/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId,
                                              HttpServletRequest req) {
        // 로그인한 사용자의 username 가져오기
        String id = req.getHeader("X-User-ID");
        Long userId = Long.valueOf(id);

        reviewCommentService.deleteComment(commentId, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reviewId/{reviewId}")
    public ResponseEntity<List<ReviewCommentResponseDTO>> viewComment(@PathVariable("reviewId") Long reviewId,
                                                                      HttpServletRequest req) {
        // 로그인한 사용자의 username 가져오기
        String id = req.getHeader("X-User-ID");
        Long userId = Long.valueOf(id);;

        List<ReviewCommentResponseDTO> comments = reviewCommentService.viewComment(reviewId, userId);

        return ResponseEntity.ok(comments);
    }
}
