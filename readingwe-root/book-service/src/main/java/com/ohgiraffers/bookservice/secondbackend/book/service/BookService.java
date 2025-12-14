package com.ohgiraffers.bookservice.secondbackend.book.service;

import com.ohgiraffers.bookservice.secondbackend.book.dto.request.AuthorRequestDTO;
import com.ohgiraffers.bookservice.secondbackend.book.dto.request.CategoryRequestDTO;
import com.ohgiraffers.bookservice.secondbackend.book.dto.request.TitleRequestDTO;
import com.ohgiraffers.bookservice.secondbackend.book.dto.response.BookResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.book.entity.Book;
import com.ohgiraffers.bookservice.secondbackend.book.entity.BookCategory;
import com.ohgiraffers.bookservice.secondbackend.book.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService( BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    private BookResponseDTO convert(Book book) {
        return BookResponseDTO.builder()
                .bookid(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .publishedDate(book.getPublishedDate())
                .category(book.getCategory())
                .build();
    }

    @Transactional
    public void deleteBook(Long bookId) {
        bookRepository.deleteById(bookId);
    }

    @Transactional
    public BookResponseDTO createBook(Book book) {
        return convert(bookRepository.save(book));
    }

    @Transactional(readOnly = true)
    public Page<Book> findAll(Pageable pageable) {

        try {
            return bookRepository.findAll(pageable);
        } catch (Exception e) {
            throw new BookQueryException("책 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public BookResponseDTO findById(Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        return convert(book);
    }

    @Transactional(readOnly = true)
    public Page<BookResponseDTO> findByTitle(String title, Pageable pageable) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title은 비어 있을 수 없습니다.");
        }

        try {
            return bookRepository.findByTitle(title, pageable)
                    .map(this::convert);
        } catch (Exception e) {
            throw new BookQueryException("제목 검색 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<BookResponseDTO> findByCategory(BookCategory category, Pageable pageable) {


        if (category == null) {
            throw new IllegalArgumentException("category는 필수입니다.");
        }

        try {
            return bookRepository.findByCategory(category, pageable)
                    .map(this::convert);
        } catch (Exception e) {
            throw new BookQueryException("카테고리 검색 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<BookResponseDTO> findByAuthor(String author, Pageable pageable) {

        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("author는 비어 있을 수 없습니다.");
        }

        try {
            return bookRepository.findByAuthor(author, pageable)
                    .map(this::convert);
        } catch (Exception e) {
            throw new BookQueryException("저자 검색 중 오류가 발생했습니다.", e);
        }
    }
//    public List<BookResponseDTO> getSortedBooks(String sortBy, String direction) {
//
//        Sort sort = direction.equalsIgnoreCase("desc")
//                ? Sort.by(sortBy).descending()
//                : Sort.by(sortBy).ascending();
//
//        return bookRepository.findAll(sort).stream()
//                .map(this::convert)
//                .toList();
//    }
}
