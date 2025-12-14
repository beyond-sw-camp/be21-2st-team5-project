package com.ohgiraffers.readingclubservice.secondbackend.readingclub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reading_club_join_request")
public class ReadingClubJoin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "club_id", nullable = false)
    private Long clubId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private JoinRequestStatus status; // PENDING, APPROVED, REJECTED
    @Column(name = "message")
    private String message;
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    public void setStatus(JoinRequestStatus status) {
        this.status = status;
    }
}
