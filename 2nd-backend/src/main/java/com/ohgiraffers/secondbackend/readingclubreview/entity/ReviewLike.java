package com.ohgiraffers.secondbackend.readingclubreview.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import com.ohgiraffers.secondbackend.user.entity.User;
import com.ohgiraffers.secondbackend.readingclubreview.entity.ReadingClubReview;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reading_club_review_like")
@Getter
@NoArgsConstructor
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_like_id")
    private Long reviewLikeId;

    @Column(name = "user_id")   // 컬럼 이름은 그대로
    private Long userId;

    // 여러 좋아요(N) : 한 리뷰(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_review_id", nullable = false)
    private ReadingClubReview review;

    @CreationTimestamp
    @Column(name = "like_datetime", nullable = false, updatable = false)
    private LocalDateTime likeDateTime;

    @Builder
    public ReviewLike(Long user, ReadingClubReview review) {
        this.userId = user;
        this.review = review;
    }
}
