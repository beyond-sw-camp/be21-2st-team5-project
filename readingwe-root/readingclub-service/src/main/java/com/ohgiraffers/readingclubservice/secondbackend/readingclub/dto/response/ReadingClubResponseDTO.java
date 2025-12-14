package com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.response;

import com.ohgiraffers.readingclubservice.secondbackend.readingclub.entity.ReadingClubStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReadingClubResponseDTO {
    private long id;
    private String name;
    private String description;
    private ReadingClubStatus status;
    private LocalDateTime createdAt;
    private long hostUserId;
    private long categoryId;
}
