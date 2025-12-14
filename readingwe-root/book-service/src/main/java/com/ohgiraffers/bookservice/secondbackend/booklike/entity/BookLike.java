package com.ohgiraffers.bookservice.secondbackend.booklike.entity;

import com.ohgiraffers.bookservice.secondbackend.book.entity.Book;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "booklike")
public class BookLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long booklike_id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

}
