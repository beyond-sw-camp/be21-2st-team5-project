package com.ohgiraffers.readingclubservice.secondbackend.client.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long userId;
    private String nickName;
    private String username;
    private String role;

}