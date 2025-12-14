package com.ohgiraffers.bookservice.secondbackend.bookreport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookReportLikeRequestDTO {
    private Long bookReportId;
    private Long userId;
}
