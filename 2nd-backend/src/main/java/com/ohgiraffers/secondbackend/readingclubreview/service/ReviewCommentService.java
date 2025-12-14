package com.ohgiraffers.secondbackend.readingclubreview.service;

import com.ohgiraffers.secondbackend.readingclubreview.dto.request.ReviewCommentRequestDTO;
import com.ohgiraffers.secondbackend.readingclubreview.dto.response.ReviewCommentResponseDTO;
import com.ohgiraffers.secondbackend.readingclubreview.entity.ReadingClubReview;
import com.ohgiraffers.secondbackend.readingclubreview.entity.ReviewComment;
import com.ohgiraffers.secondbackend.readingclubreview.repository.ReadingClubReviewRepository;
import com.ohgiraffers.secondbackend.readingclubreview.repository.ReviewCommentRepository;
import com.ohgiraffers.secondbackend.user.entity.User;
import com.ohgiraffers.secondbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewCommentService {

    private final UserRepository userRepository;
    private final ReadingClubReviewRepository reviewRepository;
    private final ReviewCommentRepository commentRepository;

    @Transactional
    public ReviewCommentResponseDTO createReviewComment(Long reviewId,
                                                        ReviewCommentRequestDTO request,
                                                        String username) {

        // 1. 유저 찾기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));

        Long userId = user.getId();
        // 2. 리뷰 찾기
        ReadingClubReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        // 3. 부모 댓글 검증 (있다면)
        Long parentCommentId = request.getParentCommentId();
        ReviewComment parent = null;

        if (parentCommentId != null) {
            parent = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));

            // 부모 댓글이 같은 리뷰에 속한 댓글인지
            if (!parent.getReview().getReviewId().equals(reviewId)) {
                throw new IllegalArgumentException("해당 리뷰의 댓글에만 대댓글을 작성할 수 있습니다.");
            }

            // 부모가 이미 대댓글이면 또 대댓글 불가
            if (parent.getParent() != null) {
                throw new IllegalStateException("대댓글에 또 대댓글을 작성할 수 없습니다.");
            }
        }

        // 4. 댓글 생성
        ReviewComment comment = ReviewComment.builder()
                .review(review)
                .user(userId)
                .parent(parent)
                .commentDetail(request.getCommentDetail())
                .build();

        ReviewComment saved = commentRepository.save(comment);

        return ReviewCommentResponseDTO.from(saved);
    }

    @Transactional
    public ReviewCommentResponseDTO modifyComment(Long commentId,
                                                  ReviewCommentRequestDTO request,
                                                  String username) {
        // 1. username으로 유저 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        Long userId = user.getId();
        // 2. 이 유저가 작성한 해당 댓글 찾기 (아니면 권한 없음)
        ReviewComment comment = commentRepository
                .findByReviewCommentIdAndUser(commentId, userId)
                .orElseThrow(() -> new AccessDeniedException("해당 댓글을 수정할 수 있는 권한이 없습니다."));

        // 내용 수정
        comment.updateContent(request.getCommentDetail());

        ReviewComment saved = commentRepository.save(comment);
        return ReviewCommentResponseDTO.from(saved);

    }

    @Transactional
    public void deleteComment(Long commentId, String username) {

        // 유저 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Long userId = user.getId();
        // 이 유저가 쓴 해당 댓글 찾기
        ReviewComment comment = commentRepository
                .findByReviewCommentIdAndUser(commentId, userId)
                .orElseThrow(() -> new AccessDeniedException("해당 댓글을 삭제할 수 있는 권한이 없습니다."));

        comment.softDelete();
    }

    @Transactional
    public List<ReviewCommentResponseDTO> viewComment(Long reviewId, String username) {

        List<ReviewComment> comments =
                commentRepository.findByReview_ReviewIdOrderByCreatedAtDesc(reviewId);

        // 일반 댓글만 필터링
        List<ReviewComment> parents = comments.stream()
                .filter(c -> c.getParent() == null)
                .toList();

        List<ReviewCommentResponseDTO> result = new ArrayList<>();

        for (ReviewComment parent : parents) {
            // 부모 댓글 추가
            result.add(ReviewCommentResponseDTO.from(parent));

            // 대댓글 필터링
            List<ReviewComment> children = comments.stream()
                    .filter(c -> parent.getReviewCommentId().equals(
                            c.getParent() != null ? c.getParent().getReviewCommentId() : null))
                    .toList();

            // 대댓글 추가
            children.forEach(child -> result.add(ReviewCommentResponseDTO.from(child)));
        }

        return result;
    }
}
