package com.ohgiraffers.secondbackend.bookreport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookReportCommentRequestDTO {
    private Long bookReportId;  //어느 독후감 댓글인지
    private Long userId;    //댓글 작성자
    private String content; // 댓글 내용
    private Long parentId;  //대댓글일때 부모 id, 없으면 null

}
