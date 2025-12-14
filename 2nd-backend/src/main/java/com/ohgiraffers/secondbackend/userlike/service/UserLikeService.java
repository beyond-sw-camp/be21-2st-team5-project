package com.ohgiraffers.secondbackend.userlike.service;


import com.ohgiraffers.secondbackend.book.entity.Book;
import com.ohgiraffers.secondbackend.book.entity.BookCategory;
import com.ohgiraffers.secondbackend.user.entity.User;
import com.ohgiraffers.secondbackend.user.repository.UserRepository;
import com.ohgiraffers.secondbackend.user.util.JWTUtil;
import com.ohgiraffers.secondbackend.userlike.dto.response.UserLikeResponseDTO;
import com.ohgiraffers.secondbackend.userlike.entity.UserLikeEntity;
import com.ohgiraffers.secondbackend.userlike.repository.UserLikeRepository;
import jakarta.transaction.Transactional;
import jdk.jfr.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLikeService {


    private final UserLikeRepository userLikeRepository;
    private final JWTUtil jWTUtil;
    private final UserRepository userRepository;

    public UserLikeService(UserLikeRepository userLikeRepository
            , JWTUtil jWTUtil
            , UserRepository userRepository){
        this.userLikeRepository = userLikeRepository;
        this.jWTUtil = jWTUtil;
        this.userRepository = userRepository;
    }

    //등록
    @Transactional
    public UserLikeResponseDTO likeBook(String accessToken, BookCategory bookCategory){

        String username=jWTUtil.getUsername(accessToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (userLikeRepository.existsByUserAndBookCategory(user, bookCategory)) {
            throw new IllegalArgumentException("이미 선호한 카테고리입니다.");
        }

        UserLikeEntity userLikeEntity = UserLikeEntity.builder()
                .user(user)
                .bookCategory(bookCategory)
                .build();

        UserLikeEntity savedUserLikeEntity = userLikeRepository.save(userLikeEntity);

        return UserLikeResponseDTO.builder()
                .userLikeId(savedUserLikeEntity.getUserLikeId())
                .userId(user.getId())
                .category(bookCategory)
                .build();

    }

    @Transactional
    public void unlikeBook(String accessToken, BookCategory bookCategory) {

        String username = jWTUtil.getUsername(accessToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        UserLikeEntity entity = userLikeRepository
                .findByUserAndBookCategory(user, bookCategory)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 좋아요한 기록이 없습니다."));

        userLikeRepository.delete(entity);
    }


    public List<BookCategory> selectCategoryAll(String accessToken) {

        // 1. 토큰에서 username 추출
        String username = jWTUtil.getUsername(accessToken);

        // 2. User 조회
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
