package com.ohgiraffers.secondbackend.bookreport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BookReportCommentResponseDTO {
    private Long commentId;
    private Long bookReportId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long parentId;  //부모 댓글 id, 없으면 null
}
