package com.ohgiraffers.secondbackend.booklike.service;


import com.ohgiraffers.secondbackend.book.entity.Book;
import com.ohgiraffers.secondbackend.book.repository.BookRepository;
import com.ohgiraffers.secondbackend.booklike.dto.request.LikeApplyDTO;
import com.ohgiraffers.secondbackend.booklike.dto.response.BookLikeResponseDTO;
import com.ohgiraffers.secondbackend.booklike.dto.response.BookRankingResponseDTO;
import com.ohgiraffers.secondbackend.booklike.entity.BookLike;
import com.ohgiraffers.secondbackend.booklike.repository.BookLikeRepository;
import com.ohgiraffers.secondbackend.user.entity.User;
import com.ohgiraffers.secondbackend.user.repository.UserRepository;
import com.ohgiraffers.secondbackend.user.util.JWTUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BooklikeService {

    private final BookLikeRepository bookLikeRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final JWTUtil jWTUtil;


    public BooklikeService(BookLikeRepository bookLikeRepository, BookRepository bookRepository, UserRepository userRepository, JWTUtil jWTUtil) {
       this.bookLikeRepository = bookLikeRepository;
       this.bookRepository = bookRepository;
       this.userRepository = userRepository;
        this.jWTUtil = jWTUtil;
    }


    @Transactional
    public BookLikeResponseDTO likeBook(String accessToken, long bookId) {

        String username = jWTUtil.getUsername(accessToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        if (bookLikeRepository.existsByUserAndBook(user, book)) {
            throw new IllegalArgumentException("이미 좋아요를 누른 책입니다.");
        }

        BookLike bookLike = BookLike.builder()
                .book(book)
                .user(user)
                .build();

        BookLike savedBookLike = bookLikeRepository.save(bookLike);

        return BookLikeResponseDTO.builder()
                .bookLikeId(savedBookLike.getBooklike_id())
                .userId(user.getId())
                .bookId(bookId)
                .build();
    }

    @Transactional
    public void deleteLike(String accessToken, long bookId) {

        String username = jWTUtil.getUsername(accessToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        BookLike bookLike = bookLikeRepository
                .findByUser_IdAndBook_BookId(user.getId(), bookId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요한 기록이 없습니다."));

        bookLikeRepository.delete(bookLike);
    }

    public List<BookRankingResponseDTO> getBookRanking() {
        List<Object[]> results = bookLikeRepository.findBooksOrderByLikeCount();

        return results.stream()
                .map(row -> {
                    Book book = (Book) row[0];
                    Long likeCount = (Long) row[1];

                    return BookRankingResponseDTO.builder()
                            .bookId(book.getBookId())
                            .title(book.getTitle())
                            .author(book.getAuthor())
                            .publisher(book.getPublisher())
                            .likeCount(likeCount)
                            .build();
                })
                .toList();
    }







}
