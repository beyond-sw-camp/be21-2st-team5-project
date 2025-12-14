package com.ohgiraffers.secondbackend.bookreport.repository;

import com.ohgiraffers.secondbackend.bookreport.entity.BookReport;
import com.ohgiraffers.secondbackend.bookreport.entity.BookReportLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookReportLikeRepository extends JpaRepository<BookReportLike, Long> {
    boolean existsByBookReportAndUserId(BookReport bookReport, Long userId);

    void deleteByBookReportAndUserId(BookReport bookreport, Long userId);

    int countByBookReport(BookReport bookreport);
}
