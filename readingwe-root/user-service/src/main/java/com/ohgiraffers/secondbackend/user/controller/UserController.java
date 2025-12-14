package com.ohgiraffers.secondbackend.user.controller;

import com.ohgiraffers.secondbackend.user.dto.request.PasswordUpdateDTO;
import com.ohgiraffers.secondbackend.user.dto.request.ProfileUpdateDTO;
import com.ohgiraffers.secondbackend.user.dto.response.UserProfileResponse;
import com.ohgiraffers.secondbackend.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "로그아웃, 회원 정보 수정, 조회 API")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest req){
        String username = req.getHeader("X-User-Name");
        String accessToken = req.getHeader("Authorization");

        if(accessToken == null || !accessToken.startsWith("Bearer ")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("AccessToken이 없습니다.");
        }

        accessToken = accessToken.substring(7);

        if(username == null || username.isBlank()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증정보가 없습니다.");
        }

        userService.logout(accessToken, username);
        return ResponseEntity.ok("logout 성공");
    }


    @PatchMapping("/update-nickname")
    public ResponseEntity<String> updateNickname(
            HttpServletRequest req
            , @RequestBody ProfileUpdateDTO profileUpdateDTO) {
        String username = req.getHeader("X-User-Name");

        if(username==null || username.isBlank()){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("인증정보가 없습니다.");
        }

        userService.updateNickname(username,profileUpdateDTO);
        return ResponseEntity.ok("닉네임 업데이트 성공");
    }

    @PatchMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            HttpServletRequest req
            , @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        String username=req.getHeader("X-User-Name");

        if(username==null || username.isBlank()){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("인증정보가 없습니다.");
        }

        userService.updatePassword(username,passwordUpdateDTO);
        return ResponseEntity.ok("비밀번호 업데이트 성공");
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfileByUsername(
            HttpServletRequest req,
            @PathVariable("username") String username) {
            UserProfileResponse profile
                    = userService.getProfileByUsername(username);
            return ResponseEntity.ok(profile);
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfileById(
            HttpServletRequest req,
            @PathVariable("userId") Long userId) {

        UserProfileResponse profile=
                userService.getProfileById(userId);
        return ResponseEntity.ok(profile);
    }


}