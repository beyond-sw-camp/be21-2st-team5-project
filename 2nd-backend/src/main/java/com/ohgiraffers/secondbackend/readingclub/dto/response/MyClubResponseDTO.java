package com.ohgiraffers.secondbackend.readingclub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyClubResponseDTO {

    private List<ReadingClubResponseDTO> hostedClubs;

    private List<ReadingClubResponseDTO> joinedClubs;
}
