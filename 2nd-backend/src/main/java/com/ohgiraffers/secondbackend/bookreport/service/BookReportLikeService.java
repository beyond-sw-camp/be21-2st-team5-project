package com.ohgiraffers.secondbackend.bookreport.service;

import com.ohgiraffers.secondbackend.bookreport.dto.request.BookReportLikeRequestDTO;
import com.ohgiraffers.secondbackend.bookreport.dto.response.BookReportLikeResponseDTO;
import com.ohgiraffers.secondbackend.bookreport.entity.BookReport;
import com.ohgiraffers.secondbackend.bookreport.entity.BookReportLike;
import com.ohgiraffers.secondbackend.bookreport.repository.BookReportLikeRepository;
import com.ohgiraffers.secondbackend.bookreport.repository.BookReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookReportLikeService {

    private final BookReportRepository bookReportRepository;
    private final BookReportLikeRepository bookReportLikeRepository;

    @Transactional
    public ResponseEntity<BookReportLikeResponseDTO> toggleLike(BookReportLikeRequestDTO request) {

        //존재하는 독후감인지 확인
        BookReport bookReport = bookReportRepository.findById(request.getBookReportId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 독후감 입니다."));

        //좋아요 눌러져 있는지 확인
        boolean alreadyLiked = bookReportLikeRepository.existsByBookReportAndUserId(bookReport, request.getUserId());

        boolean nowLiked;
        if (alreadyLiked) {
            //좋아요 취소
            bookReportLikeRepository.deleteByBookReportAndUserId(bookReport, request.getUserId());
            bookReport.decreaseLike();
            nowLiked = false;
        }else{
            // 좋아요 추가
            BookReportLike like = BookReportLike.builder()
                    .bookReport(bookReport)
                    .userId(request.getUserId())
                    .build();
            bookReportLikeRepository.save(like);
            bookReport.increaseLike();
            nowLiked = true;
        }
        int likeCount = bookReportLikeRepository.countByBookReport(bookReport);

        BookReportLikeResponseDTO responseDto = BookReportLikeResponseDTO.builder()
                .bookReportId(bookReport.getBookReportId())
                .totalLikes(likeCount)
                .liked(nowLiked)
                .build();

        return ResponseEntity.ok(responseDto);

    }



}
