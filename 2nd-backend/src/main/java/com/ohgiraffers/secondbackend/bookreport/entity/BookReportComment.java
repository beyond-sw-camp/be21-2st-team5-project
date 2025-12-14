package com.ohgiraffers.secondbackend.bookreport.entity;

import com.ohgiraffers.secondbackend.book.entity.Book;
import com.ohgiraffers.secondbackend.bookreport.dto.request.BookReportCommentRequestDTO;
import com.ohgiraffers.secondbackend.bookreport.dto.request.BookReportRequestDTO;
import com.ohgiraffers.secondbackend.bookreport.dto.response.BookReportCommentResponseDTO;
import com.ohgiraffers.secondbackend.bookreport.dto.response.BookReportResponseDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "book_report_comment")
public class BookReportComment {

    @Id
    @Column(name = "report_comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportCommentId;   //독후감 댓글id(pk)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_report_id")
    private BookReport bookReport;    //독후감 id(fk)

    @Column(name = "user_id")
    private Long userId;  //댓글 작성자 id

    @Column(name = "content")
    private String content; //댓글 내용

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;    //생성일

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;    //수정일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private BookReportComment parent;   //부모 댓글

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<BookReportComment> children = new ArrayList<>();    // 자식 댓글 목록(대댓글)

    @Builder
    public BookReportComment(BookReport bookReport, Long userId, String content, BookReportComment parent){
        this.bookReport = bookReport;
        this.userId = userId;
        this.content = content;
        this.parent = parent;
    }

    public void addChild(BookReportComment child){
        children.add(child);
        child.parent = this;
    }

    public void updateContent(String content){
        this.content = content;
    }

    public BookReportCommentResponseDTO toResponseDTO(){
        return BookReportCommentResponseDTO.builder()
                .commentId(this.reportCommentId)
                .bookReportId(this.bookReport.getBookReportId())
                .userId(this.userId)
                .content(this.content)
                .parentId(this.parent != null ? this.parent.getReportCommentId() : null)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
