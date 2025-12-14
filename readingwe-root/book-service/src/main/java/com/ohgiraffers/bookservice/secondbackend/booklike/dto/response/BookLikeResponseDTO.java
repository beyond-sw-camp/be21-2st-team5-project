package com.ohgiraffers.bookservice.secondbackend.booklike.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookLikeResponseDTO {
    private long bookLikeId;
    private long bookId;
    private long userId;
}
