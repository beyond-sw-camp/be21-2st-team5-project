package com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadingClubRequestDTO {

    private String name;
    private String description;
    private long categoryId;
}
