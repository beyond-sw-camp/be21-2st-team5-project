package com.ohgiraffers.bookservice.secondbackend.booklike.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeCancelDTO {
    private long userId;
    private long bookId;
}
