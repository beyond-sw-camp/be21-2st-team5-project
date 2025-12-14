package com.ohgiraffers.secondbackend.bookreport.service;

import com.ohgiraffers.secondbackend.bookreport.dto.request.BookReportCommentRequestDTO;
import com.ohgiraffers.secondbackend.bookreport.dto.request.BookReportRequestDTO;
import com.ohgiraffers.secondbackend.bookreport.dto.response.BookReportCommentResponseDTO;
import com.ohgiraffers.secondbackend.bookreport.entity.BookReport;
import com.ohgiraffers.secondbackend.bookreport.entity.BookReportComment;
import com.ohgiraffers.secondbackend.bookreport.repository.BookReportCommentRepository;
import com.ohgiraffers.secondbackend.bookreport.repository.BookReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookReportCommentService {

    private final BookReportRepository bookReportRepository;
    private final BookReportCommentRepository bookReportCommentRepository;

    @Transactional
    public BookReportCommentResponseDTO saveBookReportComment(BookReportCommentRequestDTO request) {

        //해당 독후감이 존재하는지 확인
        BookReport bookReport = bookReportRepository.findById(request.getBookReportId())
                .orElseThrow(() -> new IllegalArgumentException("독후감 존재하지 않음"));

        //부모 댓글 = null로 두고, 있으면 갱신함
        BookReportComment parent = null;
        //부모 댓글 있을 때
        if(request.getParentId() != null){
            //부모 댓글 parent에 넣기
            parent = bookReportCommentRepository.findById(request.getBookReportId())
                    .orElseThrow(()-> new IllegalArgumentException("부모 댓글 없음"));
        }

        //댓글 생성
        BookReportComment comment = BookReportComment.builder()
                .bookReport(bookReport)
                .userId(request.getUserId())
                .content(request.getContent())
                .parent(parent)
                .build();

        BookReportComment saved = bookReportCommentRepository.save(comment);

        return saved.toResponseDTO();
    }

    @Transactional
    public BookReportCommentResponseDTO changeBookComment(Long commentId, BookReportCommentRequestDTO request) {
        // 댓글 없을 경우
        BookReportComment comment = bookReportCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 독후감 댓글이 없습니다."));

        comment.updateContent(request.getContent());

        return  comment.toResponseDTO();
    }

    @Transactional
    public void deleteBookComment(Long commentId) {
        //삭제할 코멘트가 실제로 존재하는지 확인
        BookReportComment comment = bookReportCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 댓글이 존재하지 않습니다"));

        bookReportCommentRepository.delete(comment);
    }
}
