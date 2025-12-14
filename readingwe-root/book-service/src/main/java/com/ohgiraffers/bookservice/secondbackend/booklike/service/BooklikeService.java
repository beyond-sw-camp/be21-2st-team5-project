package com.ohgiraffers.bookservice.secondbackend.booklike.service;


import com.ohgiraffers.bookservice.secondbackend.book.entity.Book;
import com.ohgiraffers.bookservice.secondbackend.book.repository.BookRepository;
import com.ohgiraffers.bookservice.secondbackend.booklike.dto.request.LikeApplyDTO;
import com.ohgiraffers.bookservice.secondbackend.booklike.dto.request.LikeCancelDTO;
import com.ohgiraffers.bookservice.secondbackend.booklike.dto.response.BookLikeResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.booklike.dto.response.BookRankingResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.booklike.entity.BookLike;
import com.ohgiraffers.bookservice.secondbackend.booklike.repository.BookLikeRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BooklikeService {

    private final BookLikeRepository bookLikeRepository;
    private final BookRepository bookRepository;


    public BooklikeService(BookLikeRepository bookLikeRepository, BookRepository bookRepository) {
       this.bookLikeRepository = bookLikeRepository;
       this.bookRepository = bookRepository;
    }


    @Transactional
    public BookLikeResponseDTO likeBook(LikeApplyDTO likeApplyDTO) {

        Book book = bookRepository.findById(likeApplyDTO.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        if (bookLikeRepository.existsByUserIdAndBook_BookId(likeApplyDTO.getUserId(), book.getBookId())) {
            throw new IllegalArgumentException("이미 좋아요를 누른 책입니다.");
        }

        BookLike bookLike = BookLike.builder()
                .userId(likeApplyDTO.getUserId())
                .book(book)
                .build();

        BookLike savedBookLike = bookLikeRepository.save(bookLike);

        return BookLikeResponseDTO.builder()
                .bookLikeId(savedBookLike.getBooklike_id())
                .userId(likeApplyDTO.getUserId())
                .bookId(likeApplyDTO.getBookId())
                .build();
    }

    @Transactional
    public void deleteLike(LikeCancelDTO likeCancelDTO) {

        BookLike bookLike = bookLikeRepository
                .findByUserIdAndBook_BookId(likeCancelDTO.getUserId(),likeCancelDTO.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("좋아요한 기록이 없습니다."));

        bookLikeRepository.delete(bookLike);
    }

    @Transactional
    public Page<BookRankingResponseDTO> getBookRanking(Pageable pageable) {

        Page<Object[]> resultPage =
                bookLikeRepository.findBooksOrderByLikeCount(pageable);


        return resultPage.map(row -> {
            Book book = (Book) row[0];
            Long likeCount = (Long) row[1];

            if (book == null) {
                throw new IllegalStateException("랭킹 조회 중 책 정보가 없습니다.");
            }

            return BookRankingResponseDTO.builder()
                    .bookId(book.getBookId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .publisher(book.getPublisher())
                    .likeCount(likeCount)
                    .build();
        });
    }








}
