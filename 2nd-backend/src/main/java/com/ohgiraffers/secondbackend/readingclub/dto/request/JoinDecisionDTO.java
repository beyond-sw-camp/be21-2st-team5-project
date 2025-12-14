package com.ohgiraffers.secondbackend.readingclub.dto.request;

import com.ohgiraffers.secondbackend.readingclub.entity.JoinRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinDecisionDTO {
    private JoinRequestStatus status;
}
