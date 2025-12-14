package com.ohgiraffers.secondbackend.userlike.controller;

import com.ohgiraffers.secondbackend.book.entity.BookCategory;
import com.ohgiraffers.secondbackend.booklike.dto.response.BookLikeResponseDTO;
import com.ohgiraffers.secondbackend.userlike.dto.response.UserLikeResponseDTO;
import com.ohgiraffers.secondbackend.userlike.service.UserLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userlike")
@RequiredArgsConstructor
public class UserLikeController {

    private final UserLikeService userLikeService;

    @PostMapping("/like/{bookcategory}")
    public ResponseEntity<UserLikeResponseDTO> likeBook(
            @RequestHeader("Authorization")String authorizationHeader,
            @PathVariable BookCategory bookcategory
    ){
        if (!authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String accessToken = authorizationHeader.substring(7);

        UserLikeResponseDTO response=userLikeService.likeBook(accessToken,bookcategory);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/unlike/{bookcategory}")
    public ResponseEntity<Void> unlikeBook(
            @RequestHeader("Authorization")String authorizationHeader,
            @PathVariable BookCategory bookcategory
    ){
        if (!authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String accessToken = authorizationHeader.substring(7);

        userLikeService.unlikeBook(accessToken,bookcategory);

        return ResponseEntity.ok().build();

    }

    @GetMapping("/list")
    public ResponseEntity<?> getUserCategories(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        if (!authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("잘못된 Authorization 헤더");
        }

        String accessToken = authorizationHeader.substring(7);

        List<BookCategory> categories =
                userLikeService.selectCategoryAll(accessToken);

        return ResponseEntity.ok(categories);
    }


}
