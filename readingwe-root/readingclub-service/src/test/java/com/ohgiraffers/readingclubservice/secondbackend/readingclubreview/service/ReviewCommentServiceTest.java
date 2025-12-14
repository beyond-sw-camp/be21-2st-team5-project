package com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.service;

import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.dto.request.ReviewCommentRequestDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.dto.response.ReviewCommentResponseDTO;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.entity.ReadingClubReview;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.entity.ReviewComment;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.repository.ReadingClubReviewRepository;
import com.ohgiraffers.readingclubservice.secondbackend.readingclubreview.repository.ReviewCommentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ReviewCommentService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ReviewCommentServiceTest {

    @Mock
    private ReadingClubReviewRepository reviewRepository;

    @Mock
    private ReviewCommentRepository commentRepository;

    @InjectMocks
    private ReviewCommentService reviewCommentService;

    @BeforeEach
    void setUp() {
        System.out.println("setUp");
    }

    @AfterEach
    void tearDown() {
        System.out.println("tearDown");
    }

    /**
     * createReviewComment()
     * - 부모 댓글이 없는 일반 댓글 작성 성공 케이스
     */
    @Test
    void createReviewComment() {
        // given
        Long reviewId = 1L;
        Long userId = 100L;

        //  RequestDTO 는 mock 으로 만들어서 getter 만 사용
        ReviewCommentRequestDTO request = mock(ReviewCommentRequestDTO.class);
        when(request.getParentCommentId()).thenReturn(null); // 부모 댓글 없음
        when(request.getCommentDetail()).thenReturn("첫 번째 댓글입니다.");

        //  존재하는 리뷰 엔티티 준비
        ReadingClubReview review = ReadingClubReview.builder()
                .reviewTitle("테스트 리뷰")
                .reviewContent("내용")
                .build();
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        //  댓글 저장 시, ID를 채워서 반환하도록 save() 모킹
        when(commentRepository.save(any(ReviewComment.class)))
                .thenAnswer(invocation -> {
                    ReviewComment c = invocation.getArgument(0);
                    ReflectionTestUtils.setField(c, "reviewCommentId", 10L);
                    return c;
                });

        // when
        ReviewCommentResponseDTO result =
                reviewCommentService.createReviewComment(reviewId, request, userId);

        // then
        assertNotNull(result);
        assertEquals("첫 번째 댓글입니다.", result.getCommentDetail());
        assertEquals(10L, result.getReviewCommentId());

        verify(reviewRepository, times(1)).findById(reviewId);
        verify(commentRepository, times(1)).save(any(ReviewComment.class));
    }

    @Test
    void modifyComment() {
        // given
        Long commentId = 1L;
        Long userId = 100L;

        ReviewCommentRequestDTO request = mock(ReviewCommentRequestDTO.class);
        when(request.getCommentDetail()).thenReturn("수정된 댓글 내용");

        // 댓글이 속한 리뷰 엔티티 생성
        ReadingClubReview review = ReadingClubReview.builder()
                .reviewTitle("리뷰 제목")
                .reviewContent("리뷰 내용")
                .build();
        ReflectionTestUtils.setField(review, "reviewId", 1L);

        // 기존 댓글 엔티티 생성 + review 세팅
        ReviewComment comment = ReviewComment.builder()
                .commentDetail("이전 댓글 내용")
                .build();
        ReflectionTestUtils.setField(comment, "reviewCommentId", commentId);
        ReflectionTestUtils.setField(comment, "user", userId);
        ReflectionTestUtils.setField(comment, "review", review);

        when(commentRepository.findByReviewCommentIdAndUser(commentId, userId))
                .thenReturn(Optional.of(comment));

        when(commentRepository.save(any(ReviewComment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ReviewCommentResponseDTO result =
                reviewCommentService.modifyComment(commentId, request, userId);

        // then
        assertNotNull(result);
        assertEquals("수정된 댓글 내용", result.getCommentDetail());

        verify(commentRepository, times(1))
                .findByReviewCommentIdAndUser(commentId, userId);
        verify(commentRepository, times(1))
                .save(any(ReviewComment.class));
    }


    @Test
    void deleteComment() {
        // given
        Long commentId = 1L;
        Long userId = 100L;

        ReadingClubReview review = ReadingClubReview.builder()
                .reviewTitle("리뷰 제목")
                .reviewContent("리뷰 내용")
                .build();
        ReflectionTestUtils.setField(review, "reviewId", 1L);

        // real 객체 말고 spy 로 감싸서 softDelete() 호출여부를 확인
        ReviewComment comment = ReviewComment.builder()
                .commentDetail("삭제될 댓글")
                .build();
        ReflectionTestUtils.setField(comment, "reviewCommentId", commentId);
        ReflectionTestUtils.setField(comment, "user", userId);
        ReflectionTestUtils.setField(comment, "review", review);

        ReviewComment spyComment = spy(comment);

        when(commentRepository.findByReviewCommentIdAndUser(commentId, userId))
                .thenReturn(Optional.of(spyComment));

        // when
        reviewCommentService.deleteComment(commentId, userId);

        // then
        //  softDelete() 가 실제로 한 번 호출됐는지 검증
        verify(spyComment, times(1)).softDelete();

        // 리포지토리도 정상적으로 한 번 호출됐는지 검증
        verify(commentRepository, times(1))
                .findByReviewCommentIdAndUser(commentId, userId);
    }


    @Test
    void viewComment() {
        // given
        Long reviewId = 1L;
        Long loginUserId = 100L;

        // 1) 리뷰 엔티티 생성 + ID 세팅
        ReadingClubReview review = ReadingClubReview.builder()
                .reviewTitle("리뷰 제목")
                .reviewContent("리뷰 내용")
                .build();
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        // 2) 부모 댓글 생성
        ReviewComment parent = ReviewComment.builder()
                .commentDetail("부모 댓글")
                .build();
        ReflectionTestUtils.setField(parent, "reviewCommentId", 10L);
        ReflectionTestUtils.setField(parent, "user", loginUserId);
        ReflectionTestUtils.setField(parent, "review", review);

        // 3) 첫 번째 대댓글
        ReviewComment child1 = ReviewComment.builder()
                .commentDetail("대댓글 1")
                .build();
        ReflectionTestUtils.setField(child1, "reviewCommentId", 11L);
        ReflectionTestUtils.setField(child1, "user", 200L);
        ReflectionTestUtils.setField(child1, "review", review);
        ReflectionTestUtils.setField(child1, "parent", parent);

        // 4) 두 번째 대댓글
        ReviewComment child2 = ReviewComment.builder()
                .commentDetail("대댓글 2")
                .build();
        ReflectionTestUtils.setField(child2, "reviewCommentId", 12L);
        ReflectionTestUtils.setField(child2, "user", 201L);
        ReflectionTestUtils.setField(child2, "review", review);
        ReflectionTestUtils.setField(child2, "parent", parent);

        // 5) 리포지토리가 세 개의 댓글을 반환하도록 설정
        List<ReviewComment> allComments = List.of(parent, child1, child2);

        when(commentRepository.findByReview_ReviewIdOrderByCreatedAtDesc(reviewId))
                .thenReturn(allComments);

        // when
        List<ReviewCommentResponseDTO> result =
                reviewCommentService.viewComment(reviewId, loginUserId);

        // then
        // 부모 + 자식 2개 = 3개
        assertEquals(3, result.size());

        // 순서 검증: 부모 → child1 → child2
        assertEquals("부모 댓글", result.get(0).getCommentDetail());
        assertEquals("대댓글 1", result.get(1).getCommentDetail());
        assertEquals("대댓글 2", result.get(2).getCommentDetail());

        // 리뷰 타이틀도 제대로 들어갔는지 (DTO from 에서 review 사용하니까)
        assertEquals("리뷰 제목", result.get(0).getReviewTitle());
        assertEquals("리뷰 제목", result.get(1).getReviewTitle());
        assertEquals("리뷰 제목", result.get(2).getReviewTitle());

        // 리포지토리 호출 검증
        verify(commentRepository, times(1))
                .findByReview_ReviewIdOrderByCreatedAtDesc(reviewId);
    }
}
