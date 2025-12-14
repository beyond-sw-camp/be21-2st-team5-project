package com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewLikeToggleResponseDTO {
    private boolean liked;      // 지금 기준으로 좋아요 상태인지
    private long likeTotal;     // 현재 총 좋아요 수
}
