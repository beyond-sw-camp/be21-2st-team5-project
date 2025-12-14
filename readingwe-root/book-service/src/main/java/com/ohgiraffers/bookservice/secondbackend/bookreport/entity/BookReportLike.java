package com.ohgiraffers.bookservice.secondbackend.bookreport.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "book_report_like")
public class BookReportLike {

    @Id
    @Column(name = "B_Report_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long BookReportLIkeId;  //좋아요 id(pk)

    @Column(name = "user_id", nullable = false)
    private Long userId;    // 좋아요 누른 사람 id(fk)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_report_id")
    private BookReport bookReport;  //독후감id(fk)

    @Builder
    public BookReportLike(BookReport bookReport, Long userId){
        this.bookReport = bookReport;
        this.userId = userId;
    }
}
