package com.ohgiraffers.secondbackend.bookreport.repository;

import com.ohgiraffers.secondbackend.bookreport.entity.BookReportComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface BookReportCommentRepository extends JpaRepository<BookReportComment, Long> {
    // 특정 독후감의 댓글 전체 조회
    List<BookReportComment> findByBookReport_BookReportId(Long bookReportId);

    // 대댓글이 아닌 일반댓글만 조회(부모 댓글이 null)
    List<BookReportComment> findByBookReport_BookReportIdAndParentIsNull(Long bookReportId);
}
