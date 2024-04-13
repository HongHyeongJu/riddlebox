package com.labmate.riddlebox.service;

import com.labmate.riddlebox.api.ApiUserController;
import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.entity.*;
import com.labmate.riddlebox.enumpackage.*;
import com.labmate.riddlebox.repository.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.labmate.riddlebox.enumpackage.ImageType.ILLUSTRATION;

@Service
public class TimeLimitGameService {

    private final JPAQueryFactory queryFactory;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GameRecordRepository gameRecordRepository;
    private final GameContentRepository gameContentRepository;
    private final GameTextRepository gameTextRepository;
    private final GameCategoryRepository gameCategoryRepository;
    private final GameImageRepository gameImageRepository;
    private final CommentRepository commentRepository;
    private final GameSolverRepository gameSolverRepository;

    private final Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @Autowired
    public TimeLimitGameService(EntityManager em, GameRepository gameRepository,
                                UserRepository userRepository, GameRecordRepository gameRecordRepository,
                                GameContentRepository gameContentRepository, GameCategoryRepository gameCategoryRepository,
                                CommentRepository commentRepository, GameTextRepository gameTextRepository,
                                GameImageRepository gameImageRepository, GameSolverRepository gameSolverRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.gameRecordRepository = gameRecordRepository;
        this.gameContentRepository = gameContentRepository;
        this.gameCategoryRepository = gameCategoryRepository;
        this.commentRepository = commentRepository;
        this.gameTextRepository = gameTextRepository;
        this.gameImageRepository = gameImageRepository;
        this.gameSolverRepository = gameSolverRepository;
    }

    // TimeLimit Game 은 단 1건
    @Transactional(readOnly = true)
    public Long getActiveTimeLimitGameId() {
        return gameRepository.findFirstByGameCategory_IdAndStatusOrderByCreatedDateDesc(8L, GameStatus.ACTIVE).get().getId();
    }

    // TimeLimit Game 조회
    //[1]차 Spring Data JPA의 기본 메서드를 사용Spring Data JPA의 기본 메서드를 사용
    @Transactional(readOnly = true)
    public TimeLimitGameDto getTimeLimitGameDto_SpringDataJPA(Long gameId) {
        logger.info("Retrieving game details for gameId: {}", gameId);

        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new IllegalArgumentException("Game not found for ID: " + gameId));
        GameText gameText = gameTextRepository.findByGame_Id(gameId).orElseThrow(() ->
                new IllegalArgumentException("GameText not found for ID: " + gameId));
        GameImage image = gameImageRepository.findByGame_IdAndImageType(gameId, ILLUSTRATION).orElseThrow(() ->
                new IllegalArgumentException("GameImage not found for ID: " + gameId));
        GameContent gameContent = gameContentRepository.findByGame_IdAndStatus(gameId, GameStatus.ACTIVE).orElseThrow(() ->
                new IllegalArgumentException("GameContent not found for ID: " + gameId));
        GameSolver solver = gameSolverRepository.findByGame_Id(gameId).orElseThrow(() ->
                new IllegalArgumentException("GameSolver not found for ID: " + gameId));

        String nickname = null;
        if (solver.getUser() != null) {
            RBUser user = userRepository.findById(solver.getUser().getId()).orElseThrow(() ->
                    new IllegalArgumentException("User not found for ID: " + solver.getUser().getId()));
            nickname = user.getNickname();
        }

        List<Comment> comments = commentRepository.findByGameIdAndStatusOrderByCreatedDateDesc(gameId, GameStatus.ACTIVE);
        List<CommentDto> commentDtoList = comments.stream()
                .map(a -> new CommentDto(a.getCommentId(), a.getUser().getId(), a.getUser().getNickname(), a.getContent()))
                .collect(Collectors.toList());

        TimeLimitGameDto timeLimitGameDto = new TimeLimitGameDto(
                game.getId(),
                game.getTitle(),
                image.getFilePath(),
                gameText.getText(),
                gameContent.getAnswer(),
                solver.getEndDateTime(),
                commentDtoList,
                nickname
        );

        logger.info("Successfully retrieved TimeLimitGameDto with comments for gameId: {}", gameId);
        return timeLimitGameDto;
    }


    //[2]차 queryDsl + fetchJoin
//    @Transactional(readOnly = true)
//    public TimeLimitGameDto getTimeLimitGameDto_QueryDslAndFetchJoin(Long gameId, Pageable pageable){
//        var fetchResult = queryFactory
//                .select(game)
//                .from(game)
//                .leftJoin(game.gameText, gameText).fetchJoin()
//                .leftJoin(game.gameImages, gameImage).fetchJoin()
//                .leftJoin(game.gameContents, gameContent).fetchJoin()
//                .leftJoin(game.gameSolver, gameSolver).fetchJoin()
//                .leftJoin(gameSolver.user, QUser.user).fetchJoin()
//                .leftJoin(game.comments, comment).fetchJoin()
//                .leftJoin(comment.user, QUser.user).fetchJoin()
//                .where(game.id.eq(gameId))
//                .fetchOne();
//
//        // 데이터 변환 로직 (fetchResult를 TimeLimitGameDto로 변환)
//        // 변환된 TimeLimitGameDto 반환
//    }
    //[3]차 queryDsl + fetchJoin
//    @Transactional(readOnly = true)
//    public TimeLimitGameDto getTimeLimitGameDto_QueryDslVer2(Long gameId, Pageable pageable){
//
//    }


    // TimeLimit Game 생성
    @Transactional
    public void createTimeLimitGame(CreateTimeLimitGameDto createGameDto, CreateImageDto createImageDto) {
        // TIME_LIMIT 카테고리
        GameCategory gameCategory = gameCategoryRepository.findBySubject(GameSubject.TIME_LIMIT)
                .orElseThrow(() -> new EntityNotFoundException("GameCategory not found"));
        // Game 인스턴스 생성
        Game timeLimitGame = new Game(gameCategory, createGameDto.getGameTitle(),
                createGameDto.getGameDescription(),
                createGameDto.getGameLevelType(), createGameDto.getAuthor());
        // Game 저장
        gameRepository.save(timeLimitGame);

        // GameSolver 인스턴스 생성. (endTime이 LocalDate 타입)
        GameSolver gameSolver = new GameSolver(createGameDto.getGameEndTime().atStartOfDay());
        gameSolver.setGame(timeLimitGame); // GameSolver와 Game 연관 관계 설정
        // GameSolver 저장
        gameSolverRepository.save(gameSolver);

        // GameText 인스턴스 생성. (endTime이 LocalDate 타입)
        GameText gameText = new GameText(createGameDto.getGameText(), createGameDto.getExplanation());
        gameText.setGame(timeLimitGame);
        // GameText 저장
        gameTextRepository.save(gameText);

        // GameContent 인스턴스 생성.
        GameContent gameContent = new GameContent(createGameDto.getQuestion(), createGameDto.getAnswer(), 1);
        gameContent.setGame(timeLimitGame);
        // GameContent 저장
        gameContentRepository.save(gameContent);

        // GameImage 인스턴스 생성.
        GameImage gameImage = new GameImage(createImageDto.getFileOriginName(), createImageDto.getFileSaveName(), createImageDto.getImageType(),
                createImageDto.getFilePath(), createImageDto.getFileSize(), createImageDto.getFileUrl(), createImageDto.getDescription());
        gameImage.setGame(timeLimitGame);
        // GameImage 저장
        gameImageRepository.save(gameImage);

    }


    // TimeLimit Game의 Comment 생성 / 수정 / 삭제
    // TimeLimit Game의 Comment 생성
    @Transactional
    public void createCommentForTimeLimitGame(Long userId, Long gameId, CreateCommentDto commentDto) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with id: " + gameId));
        RBUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        // 댓글 생성
        Comment comment = new Comment(commentDto.getContent());
        comment.setGame(game);
        comment.setUser(user);
        commentRepository.save(comment); // 댓글 저장

        game.addComment(comment);
        user.addComment(comment);
    }

    // TimeLimit Game의 Comment 수정
    @Transactional
    public void updateCommentForTimeLimitGame(UpdateCommentDto updatedCommentDto) {
        Comment comment = commentRepository.findById(updatedCommentDto.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + updatedCommentDto.getCommentId()));
        comment.modifyComment(updatedCommentDto.getContent());
    }

    // TimeLimit Game의 Comment 삭제
    @Transactional
    public void deleteCommentFromTimeLimitGame(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        comment.deleteComment();
    }


    // TimeLimit 문제 푼 유저 기록
    @Transactional
    public void recordTimeLimitSolver(Long gameId, Long userId) {
        GameSolver gameSolver = gameSolverRepository.findByGame_Id(gameId)
                .orElseThrow(() -> new EntityNotFoundException("GameSolver not found with gameId: " + gameId));
        RBUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        gameSolver.setGameSolveUser(user);
    }


    //댓글 페이징 데이터 전달
    public Page<CommentDto> getOtherCommentsByGameId(Long gameId, Pageable pageable) {
        // Page 객체 내부의 Comment 엔티티를 CommentDto로 변환
        return commentRepository.findByGameIdAndStatusOrderByCreatedDateDesc(gameId, GameStatus.ACTIVE, pageable)
                .map(comment -> new CommentDto(
                        comment.getCommentId(),
                        comment.getUser().getId(),
                        comment.getUser().getNickname(),
                        comment.getContent()
                ));
    }


}

//    @Transactional(readOnly = true)
//    public TimeLimitGameDto getTimeLimitGameDto_SpringDataJPA(Long gameId, Pageable pageable) {
//        logger.info("Retrieving game details for gameId: {}", gameId);
//
//        Game game = gameRepository.findById(gameId).orElseThrow(() ->
//                new IllegalArgumentException("Game not found for ID: " + gameId));
//        GameText gameText = gameTextRepository.findByGame_Id(gameId).orElseThrow(() ->
//                new IllegalArgumentException("GameText not found for ID: " + gameId));
//        GameImage image = gameImageRepository.findByGame_IdAndImageType(gameId, ILLUSTRATION).orElseThrow(() ->
//                new IllegalArgumentException("GameImage not found for ID: " + gameId));
//        GameContent gameContent = gameContentRepository.findByGame_IdAndStatus(gameId, GameStatus.ACTIVE).orElseThrow(() ->
//                new IllegalArgumentException("GameContent not found for ID: " + gameId));
//        GameSolver solver = gameSolverRepository.findByGame_Id(gameId).orElseThrow(() ->
//                new IllegalArgumentException("GameSolver not found for ID: " + gameId));
//
//        String nickname = null;
//        if (solver.getUser() != null) {
//            RBUser user = userRepository.findById(solver.getUser().getId()).orElseThrow(() ->
//                    new IllegalArgumentException("User not found for ID: " + solver.getUser().getId()));
//            nickname = user.getNickname();
//        }
//
//        Page<Comment> commentPage = commentRepository.findByGameIdAndStatusOrderByCreatedDateDesc(gameId, GameStatus.ACTIVE, pageable);
//        List<CommentDto> commentDtoList = commentPage.getContent().stream()
//                .map(a -> new CommentDto(a.getCommentId(), a.getUser().getId(), a.getUser().getNickname(), a.getContent()))
//                .collect(Collectors.toList());
//
//        TimeLimitGameDto timeLimitGameDto = new TimeLimitGameDto(
//                game.getId(),
//                game.getTitle(),
//                image.getFilePath(),
//                gameText.getText(),
//                gameContent.getAnswer(),
//                solver.getEndDateTime(),
//                commentDtoList,
//                nickname,
//                commentPage.getNumber(),
//                commentPage.getTotalPages()
//        );
//
//        logger.info("Successfully retrieved TimeLimitGameDto with comments for gameId: {}", gameId);
//        return timeLimitGameDto;
//    }