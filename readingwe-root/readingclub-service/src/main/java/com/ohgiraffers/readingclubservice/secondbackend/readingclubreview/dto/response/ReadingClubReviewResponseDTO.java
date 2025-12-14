package com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.dto.response;

import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.entity.ReadingClubReview;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReadingClubReviewResponseDTO {

    private long reviewId;
    private long clubId;
    private long writerId;
    private String reviewTitle;
    private String reviewContent;
    private long likeTotal;
    private LocalDateTime createdAt;

    public static ReadingClubReviewResponseDTO from(ReadingClubReview review) {
        return ReadingClubReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .clubId(review.getClubId().getId())
                .writerId(review.getWriterId())
                .reviewTitle(review.getReviewTitle())
                .reviewContent(review.getReviewContent())
                .likeTotal(review.getLikeTotal())
                .createdAt(review.getCreatedAt())
                .build();
    }

}
