package com.ohgiraffers.secondbackend.userlike.service;


import com.ohgiraffers.secondbackend.user.entity.User;
import com.ohgiraffers.secondbackend.user.repository.UserRepository;
import com.ohgiraffers.secondbackend.user.util.JWTUtil;
import com.ohgiraffers.secondbackend.userlike.client.BookFeignClient;
import com.ohgiraffers.secondbackend.userlike.dto.response.UserLikeResponseDTO;
import com.ohgiraffers.secondbackend.userlike.entity.UserLikeEntity;
import com.ohgiraffers.secondbackend.userlike.repository.UserLikeRepository;
import jakarta.transaction.Transactional;
import jdk.jfr.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLikeService {

    private final BookFeignClient bookFeignClient;
    private final UserLikeRepository userLikeRepository;
    private final JWTUtil jWTUtil;
    private final UserRepository userRepository;

    public UserLikeService(UserLikeRepository userLikeRepository
            , JWTUtil jWTUtil
            , UserRepository userRepository
            , BookFeignClient bookFeignClient){
        this.userLikeRepository = userLikeRepository;
        this.jWTUtil = jWTUtil;
        this.userRepository = userRepository;
        this.bookFeignClient = bookFeignClient;
    }

    @Transactional
    public UserLikeResponseDTO likeBook(String username, String bookCategory) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));

        // ✅ 중복 체크
        if (userLikeRepository.existsByUserAndBookCategory(user, bookCategory)) {
            throw new IllegalStateException("이미 좋아요한 카테고리입니다.");
        }

        UserLikeEntity entity = UserLikeEntity.builder()
                .user(user)
                .bookCategory(bookCategory)
                .build();

        UserLikeEntity saved = userLikeRepository.save(entity);

        return UserLikeResponseDTO.builder()
                .userLikeId(saved.getUserLikeId())
                .userId(user.getId())
                .category(saved.getBookCategory())
                .build();
    }


    @Transactional
    public void unlikeBook(String username, String bookCategory) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));

        UserLikeEntity entity = userLikeRepository
                .findByUserAndBookCategory(user, bookCategory)
                .orElseThrow(() -> new IllegalArgumentException("좋아요 내역이 없습니다."));

        userLikeRepository.delete(entity);
    }




    public List<String> selectCategoryAll(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 3. 유저가 선호한 카테고리 엔티티 목록 조회
        List<UserLikeEntity> userLikes = userLikeRepository.findByUser(user);

        // 4. 엔티티 → 카테고리 enum 값만 추출해서 반환
        return userLikes.stream()
                .map(UserLikeEntity::getBookCategory)
                .toList();
    }



}
