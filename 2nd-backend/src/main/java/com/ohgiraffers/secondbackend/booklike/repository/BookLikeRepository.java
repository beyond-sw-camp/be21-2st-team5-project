package com.ohgiraffers.secondbackend.booklike.repository;

import com.ohgiraffers.secondbackend.book.entity.Book;
import com.ohgiraffers.secondbackend.booklike.entity.BookLike;
import com.ohgiraffers.secondbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface BookLikeRepository extends JpaRepository<BookLike, Long> {
    boolean existsByUserAndBook(User user, Book book);
    Optional<BookLike> findByUser_IdAndBook_BookId(Long userId, Long bookId);

    @Query("SELECT bl.book AS book, COUNT(bl) AS likeCount " +
            "FROM BookLike bl " +
            "GROUP BY bl.book " +
            "ORDER BY COUNT(bl) DESC")
    List<Object[]> findBooksOrderByLikeCount();
}
