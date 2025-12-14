package com.ohgiraffers.secondbackend.user.dto.response;

import com.ohgiraffers.secondbackend.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long userId;
    private String nickName;
    private String username;
    private String role;

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}
