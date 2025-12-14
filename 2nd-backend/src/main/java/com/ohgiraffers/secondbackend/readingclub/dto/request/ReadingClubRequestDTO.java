package com.ohgiraffers.secondbackend.readingclub.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadingClubRequestDTO {

    private String name;
    private String description;
    private long categoryId;
}
