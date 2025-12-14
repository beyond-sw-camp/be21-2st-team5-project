package com.ohgiraffers.bookservice.secondbackend.booklike.controller;

import com.ohgiraffers.bookservice.secondbackend.booklike.dto.request.LikeApplyDTO;
import com.ohgiraffers.bookservice.secondbackend.booklike.dto.request.LikeCancelDTO;
import com.ohgiraffers.bookservice.secondbackend.booklike.dto.response.BookLikeResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.booklike.dto.response.BookRankingResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.booklike.service.BooklikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "BookLike API", description = "책 좋아요 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/booklike")
public class BookLikeController {

    private final BooklikeService bookLikeService;

    @PostMapping("/like/{bookId}")
    public ResponseEntity<BookLikeResponseDTO> likeBook(
            HttpServletRequest req,
            @PathVariable Long bookId
    ) {
        String rawuserid = req.getHeader("X-User-Id");
        long userid=Long.parseLong(rawuserid);

        LikeApplyDTO likeApplyDTO = new LikeApplyDTO(userid,bookId);
        BookLikeResponseDTO response = bookLikeService.likeBook(likeApplyDTO);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/unlike/{bookId}")
    public ResponseEntity<Void> unlikeBook(
            HttpServletRequest req,
            @PathVariable Long bookId
    ) {
        String struserid = req.getHeader("X-User-Id");
        long userId= Long.parseLong(struserid);

        LikeCancelDTO likeCancelDTO = new LikeCancelDTO(userId,bookId);

        bookLikeService.deleteLike(likeCancelDTO);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/ranking")
    public ResponseEntity<Page<BookRankingResponseDTO>> getBookRanking(
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(bookLikeService.getBookRanking(pageable));
    }

}