package com.ohgiraffers.bookservice.secondbackend.bookreport.repository;

import com.ohgiraffers.bookservice.secondbackend.book.entity.Book;
import com.ohgiraffers.bookservice.secondbackend.bookreport.entity.BookReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookReportRepository extends JpaRepository<BookReport, Long> {

    List<BookReport> findByBookIn(List<Book> books);
}
