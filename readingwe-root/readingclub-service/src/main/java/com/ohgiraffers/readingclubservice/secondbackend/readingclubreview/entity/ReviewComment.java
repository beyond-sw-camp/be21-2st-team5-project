package com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reading_club_review_comment")
@Getter
@NoArgsConstructor
public class ReviewComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_comment_id")
    private Long reviewCommentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_review_id", nullable = false)
    private ReadingClubReview review;    // ✔ 어떤 리뷰에 달린 댓글인지

    @Column(name = "user_id", nullable = false)
    private Long user;                  // user Fk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private ReviewComment parent;       // ✔ 부모 댓글, 없으면 null

    @Column(name = "comment_detail", nullable = false)
    private String commentDetail;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "delete_comment", nullable = false)
    private boolean deleteComment = false;

    @Builder
    public ReviewComment(ReadingClubReview review,
                         Long user,
                         ReviewComment parent,
                         String commentDetail) {
        this.review = review;
        this.user = user;
        this.parent = parent;
        this.commentDetail = commentDetail;
        this.deleteComment = false;
    }

    public void updateContent(String commentDetail) {
        this.commentDetail = commentDetail;
    }

    // 소프트 삭제 메서드
    public void softDelete() {
        this.deleteComment = true;
        this.commentDetail = "삭제된 메시지입니다.";
    }

}
