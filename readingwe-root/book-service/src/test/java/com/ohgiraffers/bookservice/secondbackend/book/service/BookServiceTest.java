package com.ohgiraffers.bookservice.secondbackend.book.service;

import com.ohgiraffers.bookservice.secondbackend.book.dto.response.BookResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.book.entity.Book;
import com.ohgiraffers.bookservice.secondbackend.book.entity.BookCategory;
import com.ohgiraffers.bookservice.secondbackend.book.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    // ========================
    // findAll 테스트
    // ========================
    @Test
    @DisplayName("findAll(): 정상적으로 Page<Book> 반환")
    void testFindAllSuccess() {
        Pageable pageable = PageRequest.of(0, 10);

        Book book = createBook();
        Page<Book> page = new PageImpl<>(Collections.singletonList(book));

        when(bookRepository.findAll(pageable)).thenReturn(page);

        Page<Book> result = bookService.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("findAll(): Repository에서 오류 발생 시 BookQueryException 발생")
    void testFindAllException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findAll(pageable)).thenThrow(new RuntimeException("DB Error"));

        assertThatThrownBy(() -> bookService.findAll(pageable))
                .isInstanceOf(BookQueryException.class)
                .hasMessageContaining("책 목록 조회 중 오류가 발생했습니다.");
    }

    // ========================
    // findById 테스트
    // ========================
    @Test
    @DisplayName("findById(): 정상적으로 BookResponseDTO 반환")
    void testFindByIdSuccess() {
        Book book = createBook();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponseDTO dto = bookService.findById(1L);

        assertThat(dto).isNotNull();
        assertThat(dto.getBookid()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Test Title");
    }

    @Test
    @DisplayName("findById(): 책이 없으면 BookNotFoundException 발생")
    void testFindByIdNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(1L))
                .isInstanceOf(BookNotFoundException.class);
    }

    // ========================
    // findByTitle 테스트
    // ========================
    @Test
    @DisplayName("findByTitle(): 정상 Page<BookResponseDTO> 반환")
    void testFindByTitleSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Book book = createBook();
        Page<Book> page = new PageImpl<>(Collections.singletonList(book));

        when(bookRepository.findByTitle("Test", pageable)).thenReturn(page);

        Page<BookResponseDTO> result = bookService.findByTitle("Test", pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Title");
    }

    @Test
    @DisplayName("findByTitle(): title이 null이면 IllegalArgumentException")
    void testFindByTitleNull() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> bookService.findByTitle(null, pageable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("findByTitle(): repository exception → BookQueryException")
    void testFindByTitleRepositoryException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findByTitle("Test", pageable))
                .thenThrow(new RuntimeException("DB Error"));

        assertThatThrownBy(() -> bookService.findByTitle("Test", pageable))
                .isInstanceOf(BookQueryException.class)
                .hasMessageContaining("제목 검색 중 오류가 발생했습니다.");
    }

    // ========================
    // findByCategory 테스트
    // ========================
    @Test
    @DisplayName("findByCategory(): 정상적으로 Page<BookResponseDTO> 반환")
    void testFindByCategorySuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Book book = createBook();
        Page<Book> page = new PageImpl<>(Collections.singletonList(book));

        when(bookRepository.findByCategory(BookCategory.NOVEL, pageable)).thenReturn(page);

        Page<BookResponseDTO> result = bookService.findByCategory(BookCategory.NOVEL, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("findByCategory(): category null이면 IllegalArgumentException")
    void testFindByCategoryNull() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> bookService.findByCategory(null, pageable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("findByCategory(): repository exception → BookQueryException")
    void testFindByCategoryRepositoryError() {
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findByCategory(BookCategory.NOVEL, pageable))
                .thenThrow(new RuntimeException("DB Error"));

        assertThatThrownBy(() -> bookService.findByCategory(BookCategory.NOVEL, pageable))
                .isInstanceOf(BookQueryException.class)
                .hasMessageContaining("카테고리 검색 중 오류");
    }

    // ========================
    // findByAuthor 테스트
    // ========================
    @Test
    @DisplayName("findByAuthor(): 정상적으로 Page<BookResponseDTO> 반환")
    void testFindByAuthorSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Book book = createBook();
        Page<Book> page = new PageImpl<>(Collections.singletonList(book));

        when(bookRepository.findByAuthor("Tester", pageable)).thenReturn(page);

        Page<BookResponseDTO> result = bookService.findByAuthor("Tester", pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("findByAuthor(): author null 또는 빈 문자열이면 IllegalArgumentException")
    void testFindByAuthorInvalid() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> bookService.findByAuthor("", pageable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("findByAuthor(): repository exception → BookQueryException")
    void testFindByAuthorRepositoryError() {
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findByAuthor("Tester", pageable))
                .thenThrow(new RuntimeException("DB Error"));

        assertThatThrownBy(() -> bookService.findByAuthor("Tester", pageable))
                .isInstanceOf(BookQueryException.class)
                .hasMessageContaining("저자 검색 중 오류");
    }

    // =======================
    // 헬퍼 메소드
    // =======================
    private Book createBook() {
        return Book.builder()
                .bookId(1L)
                .title("Test Title")
                .author("Tester")
                .publisher("Test Publisher")
                .category(BookCategory.NOVEL)
                .publishedDate(new Date())
                .build();
    }
}
