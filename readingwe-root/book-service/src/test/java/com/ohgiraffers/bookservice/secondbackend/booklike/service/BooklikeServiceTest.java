package com.ohgiraffers.bookservice.secondbackend.booklike.service;

import com.ohgiraffers.bookservice.secondbackend.book.entity.Book;
import com.ohgiraffers.bookservice.secondbackend.book.entity.BookCategory;
import com.ohgiraffers.bookservice.secondbackend.book.repository.BookRepository;
import com.ohgiraffers.bookservice.secondbackend.booklike.dto.request.LikeApplyDTO;
import com.ohgiraffers.bookservice.secondbackend.booklike.dto.request.LikeCancelDTO;
import com.ohgiraffers.bookservice.secondbackend.booklike.dto.response.BookLikeResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.booklike.dto.response.BookRankingResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.booklike.entity.BookLike;
import com.ohgiraffers.bookservice.secondbackend.booklike.repository.BookLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BooklikeServiceTest {

    @Mock
    private BookLikeRepository bookLikeRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BooklikeService booklikeService;

    // ============================================
    // 1) likeBook()
    // ============================================

    @Test
    @DisplayName("likeBook(): 정상적으로 좋아요 성공")
    void testLikeBookSuccess() {

        LikeApplyDTO dto = new LikeApplyDTO(1L, 10L);

        Book book = createBook();

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(bookLikeRepository.existsByUserIdAndBook_BookId(1L, 10L)).thenReturn(false);

        BookLike newLike = BookLike.builder()
                .booklike_id(100L)
                .userId(1L)
                .book(book)
                .build();

        when(bookLikeRepository.save(any(BookLike.class))).thenReturn(newLike);

        BookLikeResponseDTO result = booklikeService.likeBook(dto);

        assertThat(result.getBookLikeId()).isEqualTo(100L);
        assertThat(result.getBookId()).isEqualTo(10L);
        assertThat(result.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("likeBook(): 책이 존재하지 않으면 예외 발생")
    void testLikeBookBookNotFound() {

        LikeApplyDTO dto = new LikeApplyDTO(1L, 10L);

        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> booklikeService.likeBook(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 책입니다.");
    }

    @Test
    @DisplayName("likeBook(): 이미 좋아요 되어 있으면 예외 발생")
    void testLikeBookAlreadyLiked() {

        LikeApplyDTO dto = new LikeApplyDTO(1L, 10L);
        Book book = createBook();

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(bookLikeRepository.existsByUserIdAndBook_BookId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> booklikeService.likeBook(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 좋아요를 누른 책입니다.");
    }


    // ============================================
    // 2) deleteLike()
    // ============================================

    @Test
    @DisplayName("deleteLike(): 정상적으로 좋아요 취소")
    void testDeleteLikeSuccess() {

        LikeCancelDTO dto = new LikeCancelDTO(1L, 10L);

        Book book = createBook();
        BookLike like = BookLike.builder()
                .booklike_id(100L)
                .userId(1L)
                .book(book)
                .build();

        when(bookLikeRepository.findByUserIdAndBook_BookId(1L, 10L))
                .thenReturn(Optional.of(like));

        booklikeService.deleteLike(dto);

        verify(bookLikeRepository, times(1)).delete(like);
    }

    @Test
    @DisplayName("deleteLike(): 좋아요 기록 없으면 예외")
    void testDeleteLikeNotFound() {

        LikeCancelDTO dto = new LikeCancelDTO(1L, 10L);

        when(bookLikeRepository.findByUserIdAndBook_BookId(1L, 10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> booklikeService.deleteLike(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("좋아요한 기록이 없습니다.");
    }

    // ============================================
    // 3) getBookRanking()
    // ============================================

    @Test
    @DisplayName("getBookRanking(): 정상적으로 랭킹 반환")
    void testGetBookRankingSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Book book = createBook();
        Object[] row = new Object[]{book, 5L};

        // 🔥 정확한 타입 생성
        List<Object[]> rows = new ArrayList<>();
        rows.add(row);

        Page<Object[]> fakePage = new FakePage<>(rows, pageable);

        when(bookLikeRepository.findBooksOrderByLikeCount(any(Pageable.class)))
                .thenReturn(fakePage);

        Page<BookRankingResponseDTO> result = booklikeService.getBookRanking(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);

        BookRankingResponseDTO dto = result.getContent().get(0);

        assertThat(dto.getBookId()).isEqualTo(book.getBookId());
        assertThat(dto.getLikeCount()).isEqualTo(5L);
    }



    // ============================================
    // Helper
    // ============================================

    private Book createBook() {
        return Book.builder()
                .bookId(10L)
                .title("Test Book")
                .author("Tester")
                .publisher("Test Publisher")
                .publishedDate(new Date())
                .category(BookCategory.NOVEL)
                .build();
    }
}


/* ============================================================
   FakePage (Spring Data Page 구현)
   — PageImpl 없이 완전한 테스트 가능하도록 제작된 버전
   ============================================================ */
class FakePage<T> implements Page<T> {

    private final List<T> content;
    private final Pageable pageable;

    public FakePage(List<T> content, Pageable pageable) {
        this.content = content;
        this.pageable = pageable;
    }

    @Override public int getTotalPages() { return 1; }
    @Override public long getTotalElements() { return content.size(); }

    @Override
    public <U> Page<U> map(java.util.function.Function<? super T, ? extends U> converter) {
        List<U> mapped = new ArrayList<>();
        for (T item : content) mapped.add(converter.apply(item));
        return new FakePage<>(mapped, pageable);
    }

    @Override public int getNumber() { return pageable.getPageNumber(); }
    @Override public int getSize() { return pageable.getPageSize(); }
    @Override public int getNumberOfElements() { return content.size(); }
    @Override public List<T> getContent() { return content; }
    @Override public boolean hasContent() { return !content.isEmpty(); }
    @Override public Sort getSort() { return pageable.getSort(); }

    @Override public boolean isFirst() { return true; }
    @Override public boolean isLast() { return true; }
    @Override public boolean hasNext() { return false; }
    @Override public boolean hasPrevious() { return false; }

    @Override public Pageable nextPageable() { return Pageable.unpaged(); }
    @Override public Pageable previousPageable() { return Pageable.unpaged(); }

    @Override public Iterator<T> iterator() { return content.iterator(); }
}
