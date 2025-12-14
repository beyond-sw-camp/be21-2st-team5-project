package com.ohgiraffers.bookservice.secondbackend.bookreport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class BookReportCommentResponseDTO {
    private Long commentId;
    private Long bookReportId;
    private Long userId;

    private String username;
    private String nickname;

    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long parentId;  //부모 댓글 id, 없으면 null

    private List<BookReportCommentResponseDTO> children ;
}
