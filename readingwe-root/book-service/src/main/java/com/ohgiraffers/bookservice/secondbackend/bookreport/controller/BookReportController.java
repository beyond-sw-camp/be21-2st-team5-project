package com.ohgiraffers.bookservice.secondbackend.bookreport.controller;

import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.request.BookReportRequestDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.response.BookReportResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.service.BookReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "BookReport API", description = "독후감을 등록, 수정, 삭제, 조회하는 API")
@RestController
@RequestMapping("/book-report")
@RequiredArgsConstructor
public class BookReportController {

    private final BookReportService bookReportService;

    //독후감 등록
    @PostMapping
    public ResponseEntity<BookReportResponseDTO> createBookReport(
            @RequestBody BookReportRequestDTO request,
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

        // 서비스로 DTO와 userId 전달
        BookReportResponseDTO response = bookReportService.saveBookReport(request, userId);

        return ResponseEntity.ok(response);
    }

    //독후감 조회(단건) - 독후감 id로 조회
    @GetMapping("/{reportId}")
    public ResponseEntity<BookReportResponseDTO> getBookReport(@PathVariable Long reportId){

        BookReportResponseDTO response = bookReportService.getBookReportById(reportId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 독후감 조회(목록) - 전체 조회
    @GetMapping
    public ResponseEntity<List<BookReportResponseDTO>> getAllBookReport(){
        List<BookReportResponseDTO> rList = bookReportService.getAllBookReports();
        return ResponseEntity.status(HttpStatus.OK).body(rList);
    }

     //독후감 수정
    @PutMapping("/{reportId}")
    public ResponseEntity<BookReportResponseDTO> modifyBookReport(
            @PathVariable Long reportId,
            @RequestBody BookReportRequestDTO request){

        BookReportResponseDTO response = bookReportService.changeBookReport(reportId, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //독후감 삭제
    @DeleteMapping("/{reportId}")
    public ResponseEntity<String> deleteBookReport(@PathVariable Long reportId){
        bookReportService.deleteBookReport(reportId);

        return ResponseEntity.status(HttpStatus.OK).body("정상적으로 삭제되었습니다.");
    }
    // 독후감 조회(책 이름으로)
    @GetMapping("/title/{bookName}")
    public ResponseEntity<List<BookReportResponseDTO>> getBookReportByBookId(@PathVariable String bookName){
        List<BookReportResponseDTO> response = bookReportService.getBookReportByBookName(bookName);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
