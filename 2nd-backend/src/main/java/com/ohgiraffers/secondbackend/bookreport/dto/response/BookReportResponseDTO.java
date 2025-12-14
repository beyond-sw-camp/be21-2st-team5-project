package com.ohgiraffers.secondbackend.bookreport.dto.response;

import com.ohgiraffers.secondbackend.bookreport.entity.BookReport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BookReportResponseDTO {
    private Long bookReportId;
    private String title;
    private String description;
    private int likeCount;
    private LocalDateTime createdAt;

    public BookReportResponseDTO(BookReport bookReport){
        this.bookReportId = bookReport.getBookReportId();
        this.title = bookReport.getTitle();
        this.description = bookReport.getDescription();
        this.likeCount = bookReport.getLikeCount();
        this.createdAt = bookReport.getCreatedAt();
    }
}
