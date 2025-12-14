package com.ohgiraffers.secondbackend.book.dto.request;

import com.ohgiraffers.secondbackend.book.entity.BookCategory;
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
