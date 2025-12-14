package com.ohgiraffers.secondbackend.bookreport.repository;

import com.ohgiraffers.secondbackend.bookreport.entity.BookReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookReportRepository extends JpaRepository<BookReport, Long> {

}
