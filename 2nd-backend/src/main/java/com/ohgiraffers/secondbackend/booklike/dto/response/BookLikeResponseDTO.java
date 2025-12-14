package com.ohgiraffers.secondbackend.booklike.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookLikeResponseDTO {
    private long bookLikeId;
    private long bookId;
    private long userId;
}
