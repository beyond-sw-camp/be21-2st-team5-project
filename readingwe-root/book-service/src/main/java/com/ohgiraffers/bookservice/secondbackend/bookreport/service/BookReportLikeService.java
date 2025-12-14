package com.ohgiraffers.bookservice.secondbackend.bookreport.service;

import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.request.BookReportLikeRequestDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.dto.response.BookReportLikeResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.bookreport.entity.BookReport;
import com.ohgiraffers.bookservice.secondbackend.bookreport.entity.BookReportLike;
import com.ohgiraffers.bookservice.secondbackend.bookreport.repository.BookReportLikeRepository;
import com.ohgiraffers.bookservice.secondbackend.bookreport.repository.BookReportRepository;
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
    public BookReportLikeResponseDTO  toggleLike(Long bookReportId, Long userId) {

        //존재하는 독후감인지 확인
        BookReport bookReport = bookReportRepository.findById(bookReportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 독후감 입니다."));

        //좋아요 눌러져 있는지 확인
        boolean alreadyLiked = bookReportLikeRepository.existsByBookReportAndUserId(bookReport, userId);

        boolean nowLiked;
        if (alreadyLiked) {
            //좋아요 취소
            bookReportLikeRepository.deleteByBookReportAndUserId(bookReport, userId);
            bookReport.decreaseLike();
            nowLiked = false;
        }else{
            // 좋아요 추가
            BookReportLike like = BookReportLike.builder()
                    .bookReport(bookReport)
                    .userId(userId)
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

        return responseDto;
    }
}
