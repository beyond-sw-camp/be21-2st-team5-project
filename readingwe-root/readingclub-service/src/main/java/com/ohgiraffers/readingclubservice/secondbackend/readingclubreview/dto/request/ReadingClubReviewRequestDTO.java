package com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadingClubReviewRequestDTO {

    private String reviewTitle;
    private String reviewContent;
}
