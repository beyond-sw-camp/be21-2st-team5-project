package com.ohgiraffers.secondbackend.bookreport.service;

import com.ohgiraffers.secondbackend.bookreport.dto.request.BookReportRequestDTO;
import com.ohgiraffers.secondbackend.bookreport.dto.response.BookReportResponseDTO;
import com.ohgiraffers.secondbackend.bookreport.entity.BookReport;
import com.ohgiraffers.secondbackend.bookreport.repository.BookReportRepository;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookReportService {

    private final BookReportRepository bookReportRepository;
//    private final UserApiClient userApiClient;    //프로젝트 분리하면
//    private final BookApiClient bookApiClient;


    // 독후감 등록 메서드
    @Transactional
    public BookReportResponseDTO saveBookReport(BookReportRequestDTO request) {
        /*
        // 1. User ID 검증
        if (!userApiClient.existsById(request.getUserId())) {
            throw new IllegalArgumentException("유효하지 않은 사용자입니다.");
        }

        // 2. Book ID 검증
        if (!bookApiClient.existsById(request.getBookId())) {
            throw new IllegalArgumentException("유효하지 않은 도서입니다.");
        }
        */
        BookReport bookReport = BookReport.builder()
                .bookId(request.getBookId())
                .userId(request.getUserId())
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        //db 저장
        BookReport saved = bookReportRepository.save(bookReport);

        //responseDTO로 변환해서 반환
        return new BookReportResponseDTO(saved);
    }

    //독후감 조회(책, 사용자로 단건조회)
    public BookReportResponseDTO getBookReportById(Long reportId) {
        BookReport bookReport = bookReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("독후감이 존재하지 않음"));

        return bookReport.toResponseDTO();
    }

    // 독후감 전체 조회
    public List<BookReportResponseDTO> getAllBookReports(){
        List<BookReport> bookReport = bookReportRepository.findAll();

        return bookReport.stream().map(BookReport::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookReportResponseDTO changeBookReport(Long reportId, BookReportRequestDTO request) {
        // 수정할 엔터티가 없을때
        BookReport bookReport = bookReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("독후감이 존재하지 않음"));

        bookReport.update(request.getTitle(), request.getDescription());

        return bookReport.toResponseDTO();
    }

    @Transactional
    public void deleteBookReport(Long reportId) {
        // 삭제할거 있는지 없는지부터 확인
        BookReport bookReport = bookReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 독후감이 존재하지 않음"));

        // 삭제
        bookReportRepository.delete(bookReport);
    }
}
