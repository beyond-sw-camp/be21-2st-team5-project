package com.ohgiraffers.bookservice.secondbackend.booklike.repository;

import com.ohgiraffers.bookservice.secondbackend.book.entity.Book;
import com.ohgiraffers.bookservice.secondbackend.booklike.entity.BookLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface BookLikeRepository extends JpaRepository<BookLike, Long> {

    boolean existsByUserIdAndBook_BookId(Long userId, Long bookId);

    Optional<BookLike> findByUserIdAndBook_BookId(Long userId, Long bookId);

    @Query("""
        SELECT bl.book, COUNT(bl)
        FROM BookLike bl
        GROUP BY bl.book
        ORDER BY COUNT(bl) DESC
    """)
    Page<Object[]> findBooksOrderByLikeCount(Pageable pageable);
}
