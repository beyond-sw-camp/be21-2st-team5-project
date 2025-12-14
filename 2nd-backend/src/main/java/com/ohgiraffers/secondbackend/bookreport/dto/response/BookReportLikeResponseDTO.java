package com.ohgiraffers.secondbackend.bookreport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BookReportLikeResponseDTO {
    private Long bookReportId;
    private boolean liked;
    private int totalLikes;

}
