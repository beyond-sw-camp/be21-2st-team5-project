package com.ohgiraffers.bookservice.secondbackend.bookreport.entity;
import com.ohgiraffers.bookservice.secondbackend.book.entity.Book;
import com.ohgiraffers.bookservice.secondbackend.client.UserProfileResponseDto;
import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.response.BookReportResponseDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Setter
@Table(name = "book_report")
public class BookReport {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookReportId;      //독후감 id(pk)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;        //도서 id(fk)

    @Column(name = "user_id")
    private Long userId;      //글 작성자 id(fk)

    @Column(name = "title")
    private String title;   //제목

    @Column(name = "description")
    private String description;     //내용

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;    //생성일

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;    //변경일

    @Column(name = "total_like")
    private int likeCount;      //좋아요 개수

    @Builder
    public BookReport(Book book, Long userId, String title, String description){
        this.book = book;
        this.userId = userId;
        this.title = title;
        this.description = description;
    }

    // setter대체용
    public void update(String title, String description){
        this.title = title;
        this.description = description;
    }

    //좋아요 개수 증가
    public void increaseLike() {
        this.likeCount++;
    }

    //좋아요 개수 감소
    public void decreaseLike() {
        this.likeCount--;
    }

    public BookReportResponseDTO toResponseDTO(UserProfileResponseDto userProfileResponseDto) {
        return BookReportResponseDTO.builder()
                .bookTittle(this.getBook().getTitle())
                .title(this.getTitle())
                .description(this.getDescription())
                .likeCount(this.getLikeCount())
                .createdAt(this.getCreatedAt())
                .username(userProfileResponseDto.getUsername())
                .nickname(userProfileResponseDto.getNickName())
                .build();
    }
}
