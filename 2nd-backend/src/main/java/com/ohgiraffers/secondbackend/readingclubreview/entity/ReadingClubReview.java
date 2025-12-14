package com.ohgiraffers.secondbackend.readingclubreview.entity;

import com.ohgiraffers.secondbackend.readingclub.entity.ReadingClub;
import com.ohgiraffers.secondbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "reading_club_review")
public class ReadingClubReview {

    @Id
    @Column(name = "club_review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)  // FK 컬럼 이름 그대로 사용
    private ReadingClub clubId;


    @Column(name = "writer_id", nullable = false)  // FK 컬럼 이름 그대로
    private Long writerId;


    @Column(name = "review_title", nullable = false)
    private String reviewTitle;

    @Column(name = "review_content", nullable = false)
    private String reviewContent;

    @Column(name = "like_total")
    private long likeTotal = 0L;

    @CreationTimestamp
    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewComment> comments = new ArrayList<>();

    @Builder
    public ReadingClubReview(ReadingClub clubId,
                             Long writerId,
                             String reviewTitle,
                             String reviewContent) {
        this.clubId = clubId;
        this.writerId = writerId;
        this.reviewTitle = reviewTitle;
        this.reviewContent = reviewContent;
        this.likeTotal = 0L;
    }

    public void update(String reviewTitle, String reviewContent) {
        this.reviewTitle = reviewTitle;
        this.reviewContent = reviewContent;
    }

    public void increaseLike() {
        this.likeTotal++;
    }

    public void decreaseLike() {
        if (this.likeTotal > 0) {
            this.likeTotal--;
        }
    }

}
