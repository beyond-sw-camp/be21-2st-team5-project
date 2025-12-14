package com.ohgiraffers.bookservice.secondbackend.bookreport.service;

import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.request.BookReportCommentRequestDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.response.BookReportCommentResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.entity.BookReport;
import com.ohgiraffers.bookservice.secondbackend.bookreport.entity.BookReportComment;
import com.ohgiraffers.bookservice.secondbackend.bookreport.repository.BookReportCommentRepository;
import com.ohgiraffers.bookservice.secondbackend.bookreport.repository.BookReportRepository;
import com.ohgiraffers.bookservice.secondbackend.client.UserClient;
import com.ohgiraffers.bookservice.secondbackend.client.UserProfileResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookReportCommentServiceTest {

    @InjectMocks
    private BookReportCommentService bookReportCommentService;

    @Mock
    private BookReportRepository bookReportRepository;

    @Mock
    private BookReportCommentRepository bookReportCommentRepository;

    @Mock
    private UserClient userClient;

    @Test
    void saveBookReportComment_success() {
        // given
        Long userId = 10L;
        BookReportCommentRequestDTO request = new BookReportCommentRequestDTO(1L, userId, "테스트 댓글", null);

        BookReport bookReport = new BookReport();
        bookReport.setBookReportId(1L);

        BookReportComment savedComment = BookReportComment.builder()
                .bookReport(bookReport)
                .userId(userId)
                .content(request.getContent())
                .build();
        savedComment.setReportCommentId(100L);

        UserProfileResponseDto userProfile = new UserProfileResponseDto("testUser", "테스트닉네임");

        // Mocking
        when(bookReportRepository.findById(request.getBookReportId())).thenReturn(Optional.of(bookReport));
        when(bookReportCommentRepository.save(any(BookReportComment.class))).thenReturn(savedComment);
        when(userClient.getUserById(userId)).thenReturn(userProfile);

        // when
        BookReportCommentResponseDTO response = bookReportCommentService.saveBookReportComment(request, userId);

        // then
        assertNotNull(response);
        assertEquals(request.getContent(), response.getContent());
        assertEquals(userProfile.getUsername(), response.getUsername());
        assertEquals(userProfile.getNickName(), response.getNickname());
        assertEquals(savedComment.getReportCommentId(), response.getCommentId());
    }

    @Test
    void changeBookComment_success() {
        // given
        Long commentId = 100L;
        Long userId = 10L;

        BookReport bookReport = new BookReport();
        bookReport.setBookReportId(1L);

        BookReportComment existingComment = BookReportComment.builder()
                .bookReport(bookReport)
                .userId(userId)
                .content("기존 댓글")
                .build();
        existingComment.setReportCommentId(commentId);

        BookReportCommentRequestDTO request = new BookReportCommentRequestDTO(1L, userId, "수정된 댓글", null);

        UserProfileResponseDto userProfile = new UserProfileResponseDto("testUser", "테스트닉네임");

        // Mocking
        when(bookReportCommentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(userClient.getUserById(userId)).thenReturn(userProfile);

        // when
        BookReportCommentResponseDTO response = bookReportCommentService.changeBookComment(commentId, request);

        // then
        assertNotNull(response);
        assertEquals(request.getContent(), response.getContent());
        assertEquals(userProfile.getUsername(), response.getUsername());
        assertEquals(userProfile.getNickName(), response.getNickname());
        assertEquals(existingComment.getReportCommentId(), response.getCommentId());
    }

    @Test
    void changeBookComment_notFound() {
        Long commentId = 999L;
        BookReportCommentRequestDTO request = new BookReportCommentRequestDTO(1L, 10L, "댓글", null);

        when(bookReportCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> bookReportCommentService.changeBookComment(commentId, request));
    }

    @Test
    void deleteBookComment_success() {
        // given
        Long commentId = 100L;
        Long userId = 10L;

        BookReport bookReport = new BookReport();
        bookReport.setBookReportId(1L);

        BookReportComment existingComment = BookReportComment.builder()
                .bookReport(bookReport)
                .userId(userId)
                .content("댓글 내용")
                .build();
        existingComment.setReportCommentId(commentId);

        // Mocking
        when(bookReportCommentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // when
        assertDoesNotThrow(() -> bookReportCommentService.deleteBookComment(commentId));

        // then
        verify(bookReportCommentRepository).delete(existingComment);
    }

    @Test
    void deleteBookComment_notFound() {
        Long commentId = 999L;

        // Mock: 댓글 없음
        when(bookReportCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        // then: 예외 발생 확인
        assertThrows(IllegalArgumentException.class,
                () -> bookReportCommentService.deleteBookComment(commentId));
    }

    @Test
    void getCommentsByReportId_success() {
        // given
        Long reportId = 1L;
        Long userId1 = 10L;
        Long userId2 = 20L;

        BookReport bookReport = new BookReport();
        bookReport.setBookReportId(reportId);

        // 루트 댓글
        BookReportComment comment1 = BookReportComment.builder()
                .bookReport(bookReport)
                .userId(userId1)
                .content("루트 댓글")
                .build();
        comment1.setReportCommentId(100L);

        // 대댓글
        BookReportComment comment2 = BookReportComment.builder()
                .bookReport(bookReport)
                .userId(userId2)
                .content("대댓글")
                .parent(comment1)
                .build();
        comment2.setReportCommentId(101L);

        // UserProfileResponseDto
        UserProfileResponseDto user1 = new UserProfileResponseDto("user1", "닉네임1");
        UserProfileResponseDto user2 = new UserProfileResponseDto("user2", "닉네임2");

        // Mock 동작 정의
        when(bookReportCommentRepository.findByBookReport_BookReportId(reportId))
                .thenReturn(java.util.List.of(comment1, comment2));
        when(userClient.getUserById(userId1)).thenReturn(user1);
        when(userClient.getUserById(userId2)).thenReturn(user2);

        // when
        List<BookReportCommentResponseDTO> responses = bookReportCommentService.getCommentsByReportId(reportId);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size()); // 루트 댓글만 최상위 리스트

        BookReportCommentResponseDTO root = responses.get(0);
        assertEquals("루트 댓글", root.getContent());
        assertEquals(1, root.getChildren().size());
        assertEquals("대댓글", root.getChildren().get(0).getContent());

        assertEquals("user1", root.getUsername());
        assertEquals("닉네임1", root.getNickname());
        assertEquals("user2", root.getChildren().get(0).getUsername());
        assertEquals("닉네임2", root.getChildren().get(0).getNickname());
    }

}