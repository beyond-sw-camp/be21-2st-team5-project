package com.ohgiraffers.secondbackend.readingclubreview.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCommentRequestDTO {

    private String commentDetail;
    private Long parentCommentId;   // null 이면 일반 댓글, 값 있으면 대댓글

}
