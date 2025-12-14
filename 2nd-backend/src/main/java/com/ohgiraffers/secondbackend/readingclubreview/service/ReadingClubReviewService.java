package com.ohgiraffers.secondbackend.readingclubreview.service;

import com.ohgiraffers.secondbackend.readingclub.entity.ReadingClub;
import com.ohgiraffers.secondbackend.readingclub.repository.ReadingClubMemberRepository;
import com.ohgiraffers.secondbackend.readingclub.repository.ReadingClubRepository;
import com.ohgiraffers.secondbackend.readingclubreview.dto.request.ReadingClubReviewRequestDTO;
import com.ohgiraffers.secondbackend.readingclubreview.dto.response.ReadingClubReviewResponseDTO;
import com.ohgiraffers.secondbackend.readingclubreview.entity.ReadingClubReview;
import com.ohgiraffers.secondbackend.readingclubreview.repository.ReadingClubReviewRepository;
import com.ohgiraffers.secondbackend.readingclubreview.repository.ReviewCommentRepository;
import com.ohgiraffers.secondbackend.readingclubreview.repository.ReviewLikeRepository;
import com.ohgiraffers.secondbackend.user.entity.User;
import com.ohgiraffers.secondbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReadingClubReviewService {

    private final UserRepository userRepository;
    private final ReadingClubReviewRepository reviewRepository;
    private final ReadingClubRepository readingClubRepository;
    private final ReadingClubMemberRepository memberRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Transactional
    public ReadingClubReviewResponseDTO createReview(Long club, ReadingClubReviewRequestDTO request, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        // 존재하는 클럽인지 체크 + 엔티티로 꺼내기
        ReadingClub existClub = readingClubRepository.findById(club)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽입니다."));

        Long clubId = existClub.getId();
        Long userId = user.getId();

        // 2) 클럽 회원인지 확인
        // 클럽 엔티티 구조 바뀐다면 수정 필요함!! userId -> user로
        boolean isMember = memberRepository.existsByClubIdAndUserId(clubId, userId);
        if (!isMember) {
            log.warn("유저 {} 는 클럽 {} 멤버가 아님", userId, clubId);
            throw new AccessDeniedException("해당 모임 참가자만 후기를 작성할 수 있습니다.");
        }

        // 3) 이미 작성한 후기 있는지 체크
        boolean alreadyWritten = reviewRepository.existsByClubIdAndWriterId(existClub, userId);
        if (alreadyWritten) {
            throw new IllegalStateException("이 모임에 이미 후기를 작성하셨습니다.");
        }

        ReadingClubReview review = ReadingClubReview.builder()
                .clubId(existClub)
                .writerId(userId)
                .reviewTitle(request.getReviewTitle())
                .reviewContent(request.getReviewContent())
                .build();

        ReadingClubReview saved = reviewRepository.save(review);

        return ReadingClubReviewResponseDTO.from(saved);
    }


    @Transactional
    public ReadingClubReviewResponseDTO modifyReview(Long reviewId, ReadingClubReviewRequestDTO request, String username) {

        // 1. username으로 유저 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));


        Long userId = user.getId();
        // 2. 이 유저가 쓴 해당 리뷰 찾기
        ReadingClubReview review = reviewRepository
                .findByReviewIdAndWriterId(reviewId, userId)
                .orElseThrow(() -> new AccessDeniedException("해당 리뷰를 수정할 수 있는 권한이 없습니다."));

        // 3. 제목/내용 수정
        review.update(request.getReviewTitle(), request.getReviewContent());

        // 4. 저장 후 DTO로 변환
        ReadingClubReview saved = reviewRepository.save(review);
        return ReadingClubReviewResponseDTO.from(saved);
    }

    @Transactional
    public void deleteReview(Long reviewId, String username) {

        // 1. username으로 유저 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));


        Long userId = user.getId();
        // 2. 이 유저가 쓴 해당 리뷰 찾기
        ReadingClubReview review = reviewRepository
                .findByReviewIdAndWriterId(reviewId, userId)
                .orElseThrow(() -> new AccessDeniedException("해당 리뷰를 삭제할 수 있는 권한이 없습니다."));

        reviewRepository.delete(review);
    }

    // ✅ 특정 모임 리뷰 – 최신순, 15개씩
    @Transactional(readOnly = true)
    public Page<ReadingClubReviewResponseDTO> getReviewsOrderByLatest(Long clubId, int page, String username) {

        // 1. username으로 유저 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        Pageable pageable = PageRequest.of(page, 15);   // page: 0부터 시작, size: 15

        Page<ReadingClubReview> result =
                reviewRepository.findByClubId_IdOrderByCreatedAtDesc(clubId, pageable);

        return result.map(ReadingClubReviewResponseDTO::from);
    }

    // ✅ 특정 모임 리뷰 – 좋아요 많은 순, 15개씩
    @Transactional(readOnly = true)
    public Page<ReadingClubReviewResponseDTO> getReviewsOrderByLike(Long clubId, int page, String username) {

        // 1. username으로 유저 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        Pageable pageable = PageRequest.of(page, 15);

        Page<ReadingClubReview> result =
                reviewRepository.findByClubId_IdOrderByLikeTotalDescCreatedAtDesc(clubId, pageable);

        return result.map(ReadingClubReviewResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public Page<ReadingClubReviewResponseDTO> getMyReviews(String username, int page) {
        // 1. username으로 유저 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        Pageable pageable = PageRequest.of(page, 15);

        Page<ReadingClubReview> reviews =
                reviewRepository.findByWriterId_OrderByCreatedAtDesc(user.getId(), pageable);

        return reviews.map(ReadingClubReviewResponseDTO::from);

    }
}
