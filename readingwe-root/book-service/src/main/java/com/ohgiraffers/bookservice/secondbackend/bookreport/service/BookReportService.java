package com.ohgiraffers.bookservice.secondbackend.bookreport.service;

import com.ohgiraffers.bookservice.secondbackend.book.entity.Book;
import com.ohgiraffers.bookservice.secondbackend.book.repository.BookRepository;
import com.ohgiraffers.bookservice.secondbackend.client.UserClient;
import com.ohgiraffers.bookservice.secondbackend.client.UserProfileResponseDto;
import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.request.BookReportRequestDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.response.BookReportResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.entity.BookReport;
import com.ohgiraffers.bookservice.secondbackend.bookreport.repository.BookReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookReportService {

    private final BookReportRepository bookReportRepository;
    private final BookRepository bookRepository;
    private final UserClient userClient;

    // 독후감 등록 메서드
    @Transactional
    public BookReportResponseDTO saveBookReport(BookReportRequestDTO request, Long userId) {


        //book엔터티 조회
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("해당 책 존재하지 않음"));

        BookReport bookReport = BookReport.builder()
                .book(book)
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        //db 저장
        BookReport saved = bookReportRepository.save(bookReport);

        UserProfileResponseDto userProfile = userClient.getUserById(saved.getUserId());

        //responseDTO로 변환해서 반환
        return saved.toResponseDTO(userProfile);
    }

    //독후감 조회(책, 사용자로 단건조회)
    public BookReportResponseDTO getBookReportById(Long reportId) {
        BookReport bookReport = bookReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("독후감이 존재하지 않음"));

        UserProfileResponseDto userProfile = userClient.getUserById(bookReport.getUserId());

        return bookReport.toResponseDTO(userProfile);
    }

    // 독후감 전체 조회
    public List<BookReportResponseDTO> getAllBookReports(){
        List<BookReport> bookReport = bookReportRepository.findAll();

        return bookReportRepository.findAll()
                .stream()
                .map(br -> {
                    UserProfileResponseDto user = userClient.getUserById(br.getUserId());
                    return br.toResponseDTO(user);
                })
                .collect(Collectors.toList());
    }

    //독후감 수정
    @Transactional
    public BookReportResponseDTO changeBookReport(Long reportId, BookReportRequestDTO request) {
        // 수정할 엔터티가 없을때
        BookReport bookReport = bookReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("독후감이 존재하지 않음"));

        bookReport.update(request.getTitle(), request.getDescription());

        UserProfileResponseDto userProfile = userClient.getUserById(bookReport.getUserId());

        return bookReport.toResponseDTO(userProfile);
    }

    @Transactional
    public void deleteBookReport(Long reportId) {
        // 삭제할거 있는지 없는지부터 확인
        BookReport bookReport = bookReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 독후감이 존재하지 않음"));

        // 삭제
        bookReportRepository.delete(bookReport);
    }

    //책 제목으로 조회
    public List<BookReportResponseDTO> getBookReportByBookName(String bookName) {
        List<Book> books = bookRepository.findByTitle(bookName);
        if (books.isEmpty()) {
            throw new IllegalArgumentException("해당 책 제목의 책이 존재하지 않습니다.");
        }

        // 2. 책 목록으로 독후감 조회
        List<BookReport> reports = bookReportRepository.findByBookIn(books);

        // 3. 사용자 정보 조회 후 DTO 변환
        return reports.stream()
                .map(report -> {
                    UserProfileResponseDto userProfile = userClient.getUserById(report.getUserId());
                    return report.toResponseDTO(userProfile);
                })
                .toList();
    }
}
