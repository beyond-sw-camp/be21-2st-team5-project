package com.ohgiraffers.secondbackend.booklike.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookRankingResponseDTO {

    private long bookId;
    private String title;
    private String author;
    private String publisher;
    private long likeCount;
}
