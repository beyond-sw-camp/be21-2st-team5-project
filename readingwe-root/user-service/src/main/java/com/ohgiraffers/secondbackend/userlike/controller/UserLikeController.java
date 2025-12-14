package com.ohgiraffers.secondbackend.userlike.controller;

import com.ohgiraffers.secondbackend.userlike.dto.request.LikeCategoryDTO;
import com.ohgiraffers.secondbackend.userlike.dto.response.UserLikeResponseDTO;
import com.ohgiraffers.secondbackend.userlike.service.UserLikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "UserLike API", description = "선호 카테고리 추가 API")
@RestController
@RequestMapping("/userlike")
@RequiredArgsConstructor
public class UserLikeController {

    private final UserLikeService userLikeService;

    @PostMapping("/like")
    public ResponseEntity<UserLikeResponseDTO> likeBook(
           HttpServletRequest req,
            @RequestBody LikeCategoryDTO likeCategoryDTO
    ) {
        String username = req.getHeader("X-User-Name");
        UserLikeResponseDTO response =
                userLikeService.likeBook(username, likeCategoryDTO.getCategory());

        return ResponseEntity.ok(response);
    }



    @DeleteMapping("/{category}")
    public ResponseEntity<Void> unlikeBook(
            HttpServletRequest req,
            @PathVariable String category
    ) {
        String username = req.getHeader("X-User-Name");
        userLikeService.unlikeBook(username, category);
        return ResponseEntity.noContent().build();
    }




    @GetMapping("/list")
    public ResponseEntity<?> getUserCategories(
            HttpServletRequest req
    ) {

        String username = req.getHeader("X-User-Name");
        List<String> categories = userLikeService.selectCategoryAll(username);

        return ResponseEntity.ok(categories);
    }


}
