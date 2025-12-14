package com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.response;

import com.ohgiraffers.readingclubservice.secondbackend.readingclub.entity.ReadingClubMemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadingClubMemberResponseDTO {

    private long id;
    private long clubId;
    private long userId;
    private ReadingClubMemberRole role;
    private LocalDateTime joinedAt;
}
