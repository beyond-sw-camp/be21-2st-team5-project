package com.ohgiraffers.bookservice.secondbackend.bookreport.controller;

import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.request.BookReportLikeRequestDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.response.BookReportLikeResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.service.BookReportLikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BookReportLike API", description = "독후감 좋아요 API")
@RestController
@RequestMapping("/book-report-like")
@RequiredArgsConstructor
public class BookReportLikeController {

    private final BookReportLikeService bookReportLikeService;

    @PostMapping("/{bookReportId}")
    public ResponseEntity<BookReportLikeResponseDTO> toggleLike(
            @PathVariable Long bookReportId,
            HttpServletRequest req) {

        String rawUserId = req.getHeader("X-User-ID");
        if (rawUserId == null || rawUserId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId;

        try {
            userId = Long.parseLong(rawUserId);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        BookReportLikeResponseDTO response = bookReportLikeService.toggleLike(bookReportId, userId);


        return ResponseEntity.ok(response);
    }

}
