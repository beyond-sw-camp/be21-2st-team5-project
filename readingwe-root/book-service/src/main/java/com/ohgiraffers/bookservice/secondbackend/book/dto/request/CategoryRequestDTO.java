package com.ohgiraffers.bookservice.secondbackend.book.dto.request;

import com.ohgiraffers.bookservice.secondbackend.book.entity.BookCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDTO {
private BookCategory category;
}
