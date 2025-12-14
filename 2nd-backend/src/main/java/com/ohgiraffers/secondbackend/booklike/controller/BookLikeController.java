package com.ohgiraffers.secondbackend.booklike.controller;

import com.ohgiraffers.secondbackend.booklike.dto.response.BookLikeResponseDTO;
import com.ohgiraffers.secondbackend.booklike.dto.response.BookRankingResponseDTO;
import com.ohgiraffers.secondbackend.booklike.service.BooklikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/booklike")
public class BookLikeController {

    private final BooklikeService bookLikeService;

    @PostMapping("/like/{bookId}")
    public ResponseEntity<BookLikeResponseDTO> likeBook(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long bookId
    ) {

        if (!authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String accessToken = authorizationHeader.substring(7);

        BookLikeResponseDTO response = bookLikeService.likeBook(accessToken, bookId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/unlike/{bookId}")
    public ResponseEntity<Void> unlikeBook(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long bookId
    ) {
        if (!authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String accessToken = authorizationHeader.substring(7);

        bookLikeService.deleteLike(accessToken, bookId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<BookRankingResponseDTO>> getBookRanking() {
        return ResponseEntity.ok(bookLikeService.getBookRanking());
    }

}

