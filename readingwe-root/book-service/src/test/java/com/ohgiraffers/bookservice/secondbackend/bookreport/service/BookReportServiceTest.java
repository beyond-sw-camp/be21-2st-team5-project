package com.ohgiraffers.bookservice.secondbackend.bookreport.service;

import com.ohgiraffers.bookservice.secondbackend.book.entity.Book;
import com.ohgiraffers.bookservice.secondbackend.book.repository.BookRepository;
import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.request.BookReportRequestDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.response.BookReportResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.entity.BookReport;
import com.ohgiraffers.bookservice.secondbackend.bookreport.repository.BookReportRepository;
import com.ohgiraffers.bookservice.secondbackend.client.UserClient;
import com.ohgiraffers.bookservice.secondbackend.client.UserProfileResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookReportServiceTest {

    @InjectMocks
    private BookReportService bookReportService;

    @Mock
    private BookReportRepository bookReportRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserClient userClient;

    @Test
    void saveBookReport_success() {
        // given
        BookReportRequestDTO request = new BookReportRequestDTO(1L, "테스트 독후감 제목", "테스트 내용");
        Long userId = 10L;

        Book book = new Book();
        book.setBookId(1L); // 엔티티에 맞게 필드 설정

        BookReport savedBookReport = BookReport.builder()
                .book(book)
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        savedBookReport.setBookReportId(100L); // 엔티티 id

        UserProfileResponseDto userProfile = new UserProfileResponseDto("testUser", "테스트닉네임");

        // Mocking
        when(bookRepository.findById(request.getBookId())).thenReturn(java.util.Optional.of(book));
        when(bookReportRepository.save(any(BookReport.class))).thenReturn(savedBookReport);
        when(userClient.getUserById(userId)).thenReturn(userProfile);

        // when
        BookReportResponseDTO response = bookReportService.saveBookReport(request, userId);

        // then
        assertNotNull(response);
        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(userProfile.getUsername(), response.getUsername());
        assertEquals(userProfile.getNickName(), response.getNickname());
    }

    @Test
    void getBookReportById_success() {
        // given
        Long reportId = 100L;
        Long userId = 10L;

        // BookReport 엔티티 생성
        BookReport bookReport = BookReport.builder()
                .book(new Book())
                .userId(userId)
                .title("테스트 독후감")
                .description("테스트 내용")
                .build();
        bookReport.setBookReportId(reportId);

        // UserProfileResponseDto 생성 (생성자 또는 Mock 사용)
        UserProfileResponseDto userProfile = new UserProfileResponseDto("testUser", "테스트닉네임");

        // Mock 동작 정의
        when(bookReportRepository.findById(reportId)).thenReturn(java.util.Optional.of(bookReport));
        when(userClient.getUserById(userId)).thenReturn(userProfile);

        // when
        BookReportResponseDTO response = bookReportService.getBookReportById(reportId);

        // then
        assertNotNull(response);
        assertEquals("테스트 독후감", response.getTitle());
        assertEquals("테스트 내용", response.getDescription());
        assertEquals("testUser", response.getUsername());
        assertEquals("테스트닉네임", response.getNickname());
    }


    @Test
    void getAllBookReports_success() {
        // given
        Long userId1 = 1L;
        Long userId2 = 2L;

        BookReport report1 = BookReport.builder()
                .book(new Book())
                .userId(userId1)
                .title("독후감 1")
                .description("내용 1")
                .build();
        report1.setBookReportId(101L);

        BookReport report2 = BookReport.builder()
                .book(new Book())
                .userId(userId2)
                .title("독후감 2")
                .description("내용 2")
                .build();
        report2.setBookReportId(102L);

        // UserProfileResponseDto 생성
        UserProfileResponseDto user1 = new UserProfileResponseDto("user1", "닉네임1");
        UserProfileResponseDto user2 = new UserProfileResponseDto("user2", "닉네임2");

        // Mock 동작 정의
        when(bookReportRepository.findAll()).thenReturn(java.util.List.of(report1, report2));
        when(userClient.getUserById(userId1)).thenReturn(user1);
        when(userClient.getUserById(userId2)).thenReturn(user2);

        // when
        List<BookReportResponseDTO> responses = bookReportService.getAllBookReports();

        // then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals("독후감 1", responses.get(0).getTitle());
        assertEquals("user1", responses.get(0).getUsername());
        assertEquals("닉네임1", responses.get(0).getNickname());

        assertEquals("독후감 2", responses.get(1).getTitle());
        assertEquals("user2", responses.get(1).getUsername());
        assertEquals("닉네임2", responses.get(1).getNickname());
    }


    @Test
    void changeBookReport_success() {
        // given
        Long reportId = 100L;
        Long userId = 10L;

        // 기존 BookReport 엔티티
        BookReport existingReport = BookReport.builder()
                .book(new Book())
                .userId(userId)
                .title("기존 제목")
                .description("기존 내용")
                .build();
        existingReport.setBookReportId(reportId);

        // 수정 요청 DTO
        BookReportRequestDTO request = new BookReportRequestDTO(1L, "수정된 제목", "수정된 내용");

        // UserProfileResponseDto
        UserProfileResponseDto userProfile = new UserProfileResponseDto("testUser", "테스트닉네임");

        // Mock 동작 정의
        when(bookReportRepository.findById(reportId)).thenReturn(java.util.Optional.of(existingReport));
        when(userClient.getUserById(userId)).thenReturn(userProfile);

        // when
        BookReportResponseDTO response = bookReportService.changeBookReport(reportId, request);

        // then
        assertNotNull(response);
        assertEquals("수정된 제목", response.getTitle());
        assertEquals("수정된 내용", response.getDescription());
        assertEquals("testUser", response.getUsername());
        assertEquals("테스트닉네임", response.getNickname());
    }


    @Test
    void deleteBookReport_success() {
        // given
        Long reportId = 100L;
        Long userId = 10L;

        BookReport existingReport = BookReport.builder()
                .book(new Book())
                .userId(userId)
                .title("제목")
                .description("내용")
                .build();
        existingReport.setBookReportId(reportId);

        // Mock 동작 정의: findById 호출 시 존재하는 엔티티 반환
        when(bookReportRepository.findById(reportId)).thenReturn(java.util.Optional.of(existingReport));

        // when
        assertDoesNotThrow(() -> bookReportService.deleteBookReport(reportId));

        // then
        // delete가 실제로 호출되는지 확인 가능 (Mockito.verify)
        verify(bookReportRepository).delete(existingReport);
    }

    @Test
    void deleteBookReport_notFound() {
        Long reportId = 999L;

        // Mock: 해당 ID 없으면 Optional.empty() 반환
        when(bookReportRepository.findById(reportId)).thenReturn(java.util.Optional.empty());

        // then: 예외 발생 확인
        assertThrows(IllegalArgumentException.class,
                () -> bookReportService.deleteBookReport(reportId));
    }


}