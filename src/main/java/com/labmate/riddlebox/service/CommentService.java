package com.labmate.riddlebox.service;

import com.labmate.riddlebox.admindto.Question;
import com.labmate.riddlebox.api.ApiUserController;
import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.entity.*;
import com.labmate.riddlebox.enumpackage.*;
import com.labmate.riddlebox.repository.CommentRepository;
import com.labmate.riddlebox.repository.GameRecordRepository;
import com.labmate.riddlebox.repository.GameRepository;
import com.labmate.riddlebox.repository.UserRepository;
import com.labmate.riddlebox.util.GameScoreResult;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.labmate.riddlebox.entity.QComment.comment;
import static com.labmate.riddlebox.entity.QGame.game;
import static com.labmate.riddlebox.entity.QGameContent.gameContent;
import static com.labmate.riddlebox.entity.QGameImage.gameImage;
import static com.labmate.riddlebox.entity.QGameText.gameText;
import static com.labmate.riddlebox.entity.QRecommendGame.recommendGame;
import static com.labmate.riddlebox.enumpackage.ImageType.ILLUSTRATION;

@Service
public class CommentService {

    private final JPAQueryFactory queryFactory;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    private final Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @Autowired
    public CommentService(EntityManager em,
                          CommentRepository commentRepository,
                          UserRepository userRepository,
                          GameRepository gameRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }


    /*  WHERE절에 사용되는 조건  */
    /* 댓글 상태 Acitve*/

    private BooleanExpression commentIsActive() {
        return comment.isActive.eq(true);
    }

    private BooleanExpression commentIdEquals(Long commentId) {
        return comment.commentId.eq(commentId);
    }

    private BooleanExpression commentGameIdEquals(Long gameId) {
        return comment.game.id.eq(gameId);
    }

    private BooleanExpression commentWriterIdEquals(Long writerId) {
        return comment.user.id.eq(writerId);
    }


    //내 댓글만 모아 보기
    @Transactional(readOnly = true)
    public Page<CommentDto> getGameCommentsPagingByUserId(Long gameId, Long userId, Pageable pageable) {
        List<Comment> comments = queryFactory
                .selectFrom(QComment.comment)
                .leftJoin(QComment.comment.user, QRBUser.rBUser).fetchJoin()
                .where(commentGameIdEquals(gameId), commentIsActive(), commentWriterIdEquals(userId))  // status 대신 is_active를 사용
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(QComment.comment.createdDate.asc())
                .fetch();


        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> new CommentDto(
                        comment.getCommentId(),
                        comment.getUser().getId(),
                        comment.getUser().getNickname(),
                        comment.getContent()))
                .collect(Collectors.toList());


        long count = queryFactory
                .select(comment.commentId.count())
                .from(QComment.comment)
                .where(commentGameIdEquals(gameId), commentIsActive(), commentWriterIdEquals(userId))
                .fetchOne();

        return new PageImpl<>(commentDtos, pageable, count);
    }


    //새 댓글 등록
    @Transactional
    public CommentDto createComment(Long userId, CreateCommentDto createCommentDto) {
        // 사용자 정보
        RBUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        // 게임 정보
        Game game = gameRepository.findById(createCommentDto.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + createCommentDto.getGameId()));


        // 새 댓글 객체 생성 및 초기화
        Comment newComment = new Comment(createCommentDto.getContent());
        newComment.setUser(user);
        newComment.setGame(game);
        // 새 댓글 저장
        Comment savedComment = commentRepository.save(newComment);

        return new CommentDto(savedComment.getCommentId(),
                                savedComment.getUser().getId(),
                                savedComment.getUser().getNickname(),
                                savedComment.getContent());
    }

//    public CommentDto updateComment(Long userId, CreateCommentDto updateCommentDto) {
//    }

    // 댓글 내용만 수정
    @Transactional
    public CommentDto modifyCommentContent(Long commentId, Long userId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        // 사용자 ID 검증
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You do not have permission to edit this comment");
        }

        // 내용 수정
        comment.modifyComment(newContent);
        Comment modifiedComment = commentRepository.save(comment);

        // 수정된 댓글 정보를 DTO로 변환하여 반환
        return new CommentDto(modifiedComment.getCommentId(),
                                modifiedComment.getUser().getId(),
                                modifiedComment.getUser().getNickname(),
                                modifiedComment.getContent());
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        // 사용자 ID 검증
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You do not have permission to edit this comment");
        }

        // 내용 수정 GameStatus.DELETED
        comment.deleteComment();
        commentRepository.save(comment);
    }


}
