package com.ohgiraffers.readingclubservice.secondbackend.readingclub.dto.request;

import com.ohgiraffers.readingclubservice.secondbackend.readingclub.entity.JoinRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinDecisionDTO {
    private JoinRequestStatus status;
}
