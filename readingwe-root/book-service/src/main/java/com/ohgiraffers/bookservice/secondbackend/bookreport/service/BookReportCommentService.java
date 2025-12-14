package com.ohgiraffers.bookservice.secondbackend.bookreport.service;

import com.ohgiraffers.bookservice.secondbackend.client.UserClient;
import com.ohgiraffers.bookservice.secondbackend.client.UserProfileResponseDto;
import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.request.BookReportCommentRequestDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.response.BookReportCommentResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.entity.BookReport;
import com.ohgiraffers.bookservice.secondbackend.bookreport.entity.BookReportComment;
import com.ohgiraffers.bookservice.secondbackend.bookreport.repository.BookReportCommentRepository;
import com.ohgiraffers.bookservice.secondbackend.bookreport.repository.BookReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookReportCommentService {

    private final BookReportRepository bookReportRepository;
    private final BookReportCommentRepository bookReportCommentRepository;
    private final UserClient userClient;

    @Transactional
    public BookReportCommentResponseDTO saveBookReportComment(BookReportCommentRequestDTO request, Long userId) {

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


        UserProfileResponseDto userProfile = userClient.getUserById(userId);

        return saved.toResponseDTO(userProfile.getUsername(), userProfile.getNickName());
    }

    @Transactional
    public BookReportCommentResponseDTO changeBookComment(Long commentId, BookReportCommentRequestDTO request) {
        // 댓글 없을 경우
        BookReportComment comment = bookReportCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 독후감 댓글이 없습니다."));

        comment.updateContent(request.getContent());

        UserProfileResponseDto userProfile = userClient.getUserById(comment.getUserId());

        return  comment.toResponseDTO(userProfile.getUsername(), userProfile.getNickName());
    }

    //댓글 삭제
    @Transactional
    public void deleteBookComment(Long commentId) {
        //삭제할 코멘트가 실제로 존재하는지 확인
        BookReportComment comment = bookReportCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 댓글이 존재하지 않습니다"));

        bookReportCommentRepository.delete(comment);
    }

    //특정 독후감 댓글 전체 조회
    @Transactional
    public List<BookReportCommentResponseDTO> getCommentsByReportId(Long reportId) {

        List<BookReportComment> comments =
                bookReportCommentRepository.findByBookReport_BookReportId(reportId);

        //1) 모든 댓글 -> DTO로 변환
        Map<Long, BookReportCommentResponseDTO> dtoMap = new HashMap<>();

        for (BookReportComment c : comments) {
            UserProfileResponseDto profile = userClient.getUserById(c.getUserId());

            BookReportCommentResponseDTO dto =
                    c.toResponseDTO(profile.getUsername(), profile.getNickName());

            dtoMap.put(c.getReportCommentId(), dto);
        }

        //계층구조 만들기
        List<BookReportCommentResponseDTO> rootList = new ArrayList<>();

        for (BookReportComment c : comments) {
            BookReportCommentResponseDTO dto = dtoMap.get(c.getReportCommentId());

            if(c.getParent() == null) {
                rootList.add(dto);
            } else {
                dtoMap.get(c.getParent().getReportCommentId())
                        .getChildren().add(dto);
            }
        }
        return rootList;
    }
}
