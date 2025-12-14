package com.ohgiraffers.secondbackend.userlike.dto.request;

import com.ohgiraffers.secondbackend.book.entity.BookCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeCategoryDTO {
    private BookCategory category;
}
