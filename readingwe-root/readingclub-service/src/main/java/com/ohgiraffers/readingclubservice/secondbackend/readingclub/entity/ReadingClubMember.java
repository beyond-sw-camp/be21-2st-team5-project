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
@Table(name = "reading_club_member")
public class ReadingClubMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "club_id", nullable = false)
    private Long clubId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReadingClubMemberRole role;
    @Column(name = "joined_at")
    @CreationTimestamp
    private LocalDateTime joinedAt;

    public void changeRole(ReadingClubMemberRole role) {
        this.role = role;
    }
}
