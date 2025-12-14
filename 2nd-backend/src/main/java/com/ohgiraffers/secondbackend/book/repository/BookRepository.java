package com.ohgiraffers.secondbackend.book.repository;

import com.ohgiraffers.secondbackend.book.dto.request.TitleRequestDTO;
import com.ohgiraffers.secondbackend.book.dto.response.BookResponseDTO;
import com.ohgiraffers.secondbackend.book.entity.Book;
import com.ohgiraffers.secondbackend.book.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book>findByTitle(String title);
    List<Book>findByCategory(BookCategory category);
    List<Book>findByAuthor(String author);

}
