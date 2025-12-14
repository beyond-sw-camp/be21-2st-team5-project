package com.ohgiraffers.secondbackend.book.dto.response;


import com.ohgiraffers.secondbackend.book.entity.BookCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class BookResponseDTO {
    private long bookid;
    private String title;
    private String author;
    private String publisher;
    private Date publishedDate;
    private BookCategory category;
}
