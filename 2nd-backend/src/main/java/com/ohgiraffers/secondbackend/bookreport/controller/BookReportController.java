package com.ohgiraffers.secondbackend.bookreport.controller;

import com.ohgiraffers.secondbackend.bookreport.dto.request.BookReportRequestDTO;
import com.ohgiraffers.secondbackend.bookreport.dto.response.BookReportResponseDTO;
import com.ohgiraffers.secondbackend.bookreport.entity.BookReport;
import com.ohgiraffers.secondbackend.bookreport.service.BookReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book-report")
@RequiredArgsConstructor
public class BookReportController {

    private final BookReportService bookReportService;

    //독후감 등록
    @PostMapping()
    public ResponseEntity<BookReportResponseDTO> createBookReport(
            @RequestBody BookReportRequestDTO request){

        BookReportResponseDTO response = bookReportService.saveBookReport(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
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


}
