package com.ohgiraffers.readingclubservice.secondbackend.readingclub.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "reading_club")
public class ReadingClub {
    @Id
    @Column(name = "club_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "club_name", nullable = false)
    private String name;
    @Column(name = "club_description")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "club_status", nullable = false)
    private ReadingClubStatus status;
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "host_user_id", nullable = false)  // user 테이블 fk
    private long userId;
    @Column(name = "category", nullable = false)      // category 테이블 fk
    private long categoryId;
    @Column(name = "max_member")
    private int maxMember = 5;
    @Column(name = "current_member")
    private int currentMember = 0;

    @Builder
    public ReadingClub(String name, String description, long userId, long categoryId, ReadingClubStatus status) {
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.categoryId = categoryId;
        this.status = status;
    }

    public void changeStatus(ReadingClubStatus status) {
        this.status = status;
    }

    public void update(String name, String description, Long categoryId) {
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
    }

    public void finish(){
        this.status = ReadingClubStatus.FINISHED;
    }

    public void addMember(){
        if(status == ReadingClubStatus.FINISHED){
            throw new IllegalStateException("이미 종료된 모임입니다.");
        }
        if (currentMember >= maxMember){
            throw new IllegalStateException("이미 정원이 가득 찬 모임입니다.");
        }

        currentMember++;

        if(currentMember >= maxMember){
            status = ReadingClubStatus.CLOSED;
        }
    }

    public void removeMember() {
        if (currentMember <= 0) {
            throw new IllegalStateException("모임 인원이 0명입니다.");
        }
        if (status == ReadingClubStatus.FINISHED) {
            throw new IllegalStateException("종료된 모임에서는 탈퇴할 수 없습니다.");
        }
        currentMember--;
        if (status == ReadingClubStatus.CLOSED && currentMember < maxMember) {
            status = ReadingClubStatus.OPEN;
        }
    }
}
