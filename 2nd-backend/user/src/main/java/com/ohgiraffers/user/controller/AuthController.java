package com.ohgiraffers.user.controller;

import com.ohgiraffers.secondbackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    //회원가입

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Map<String, String> request){
        String username=request.get("username");
        String password= request.get("password");
        String nickname=request.get("nickname");
        try{
            userService.signup(username,password,nickname);
            return ResponseEntity.ok("등록 성공");
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody Map<String,String> request){
        String username=request.get("username");
        String password=request.get("password");

        try{
            String[] tokens=userService.login(username,password);
            return ResponseEntity.ok(Map.of(
                    "accessToken",tokens[0],
                    "refreshToken",tokens[1]
            ));
        }catch(Exception e){
            return ResponseEntity.status(401).body(Map.of("error",e.getMessage()));
        }
    }

}
