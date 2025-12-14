package com.ohgiraffers.secondbackend.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateDTO {
    private String nickname;   // 보통 닉네임만 수정 가능
}
