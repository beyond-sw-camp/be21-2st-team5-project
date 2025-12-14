package com.ohgiraffers.secondbackend.bookreport.controller;

import com.ohgiraffers.secondbackend.bookreport.dto.request.BookReportCommentRequestDTO;
import com.ohgiraffers.secondbackend.bookreport.dto.request.BookReportRequestDTO;
import com.ohgiraffers.secondbackend.bookreport.dto.response.BookReportCommentResponseDTO;
import com.ohgiraffers.secondbackend.bookreport.service.BookReportCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book-report-comment")
@RequiredArgsConstructor
public class BookReportCommentController {

    private final BookReportCommentService bookReportCommentService;

    // 댓글 등록
    @PostMapping
    public ResponseEntity<BookReportCommentResponseDTO> createComment(
            @RequestBody BookReportCommentRequestDTO request){

        BookReportCommentResponseDTO response = bookReportCommentService.saveBookReportComment(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<BookReportCommentResponseDTO> updateComment(
            @RequestBody BookReportCommentRequestDTO request,
            @PathVariable Long commentId){

        BookReportCommentResponseDTO response = bookReportCommentService.changeBookComment(commentId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId){
        bookReportCommentService.deleteBookComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).body("정상적으로 댓글이 삭제됩니다.");
    }


}
