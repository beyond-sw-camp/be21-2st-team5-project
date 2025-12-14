package com.ohgiraffers.secondbackend.userlike.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLikeResponseDTO {
    private long userLikeId;
    private long userId;
    private String category;
}
