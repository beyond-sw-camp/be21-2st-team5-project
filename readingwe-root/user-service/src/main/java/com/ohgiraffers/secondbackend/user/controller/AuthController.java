package com.ohgiraffers.secondbackend.user.controller;

import com.ohgiraffers.secondbackend.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth API", description = "회원가입, 로그인 API")
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
            return ResponseEntity.badRequest().body("등록실패! : "+e.getMessage());
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
            return ResponseEntity.status(401).body(Map.of("로그인이 불가능합니다 : ",e.getMessage()));
        }
    }

}
