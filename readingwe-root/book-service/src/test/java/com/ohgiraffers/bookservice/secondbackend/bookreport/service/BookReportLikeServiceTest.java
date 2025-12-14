package com.ohgiraffers.bookservice.secondbackend.bookreport.service;

import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.response.BookReportLikeResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.entity.BookReport;
import com.ohgiraffers.bookservice.secondbackend.bookreport.entity.BookReportLike;
import com.ohgiraffers.bookservice.secondbackend.bookreport.repository.BookReportLikeRepository;
import com.ohgiraffers.bookservice.secondbackend.bookreport.repository.BookReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookReportLikeServiceTest {

    @InjectMocks
    private BookReportLikeService bookReportLikeService;

    @Mock
    private BookReportRepository bookReportRepository;

    @Mock
    private BookReportLikeRepository bookReportLikeRepository;

    @Test
    void toggleLike_addLike() {
        // given
        Long bookReportId = 1L;
        Long userId = 10L;

        BookReport bookReport = BookReport.builder().build();
        bookReport.setBookReportId(bookReportId);

        when(bookReportRepository.findById(bookReportId)).thenReturn(Optional.of(bookReport));
        when(bookReportLikeRepository.existsByBookReportAndUserId(bookReport, userId)).thenReturn(false);
        when(bookReportLikeRepository.countByBookReport(bookReport)).thenReturn(1);

        // when
        BookReportLikeResponseDTO response = bookReportLikeService.toggleLike(bookReportId, userId);

        // then
        assertNotNull(response);
        assertEquals(bookReportId, response.getBookReportId());
        assertTrue(response.isLiked());
        assertEquals(1, response.getTotalLikes());

        verify(bookReportLikeRepository).save(any(BookReportLike.class));
    }

    @Test
    void toggleLike_removeLike() {
        // given
        Long bookReportId = 1L;
        Long userId = 10L;

        BookReport bookReport = BookReport.builder().build();
        bookReport.setBookReportId(bookReportId);

        when(bookReportRepository.findById(bookReportId)).thenReturn(Optional.of(bookReport));
        when(bookReportLikeRepository.existsByBookReportAndUserId(bookReport, userId)).thenReturn(true);
        when(bookReportLikeRepository.countByBookReport(bookReport)).thenReturn(0);

        // when
        BookReportLikeResponseDTO response = bookReportLikeService.toggleLike(bookReportId, userId);

        // then
        assertNotNull(response);
        assertEquals(bookReportId, response.getBookReportId());
        assertFalse(response.isLiked());
        assertEquals(0, response.getTotalLikes());

        verify(bookReportLikeRepository).deleteByBookReportAndUserId(bookReport, userId);
    }

    @Test
    void toggleLike_reportNotFound() {
        Long bookReportId = 999L;
        Long userId = 10L;

        when(bookReportRepository.findById(bookReportId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> bookReportLikeService.toggleLike(bookReportId, userId));
    }
}