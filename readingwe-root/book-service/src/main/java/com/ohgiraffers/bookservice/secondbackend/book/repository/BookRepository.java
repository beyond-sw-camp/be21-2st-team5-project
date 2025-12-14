package com.ohgiraffers.bookservice.secondbackend.book.repository;

import com.ohgiraffers.bookservice.secondbackend.book.entity.Book;
import com.ohgiraffers.bookservice.secondbackend.book.entity.BookCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByTitle(String title, Pageable pageable);

    Page<Book> findByAuthor(String author, Pageable pageable);

    Page<Book> findByCategory(BookCategory category, Pageable pageable);

    //bookReport용
    List<Book> findByTitle(String title);
}

