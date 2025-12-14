package com.ohgiraffers.bookservice.secondbackend.bookreport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookReportRequestDTO {
    private Long bookId;    //도서 id
//    private Long userId;    //작성자 id
    private String title;   //독후감 제목
    private String description; //독후감 내용
}
