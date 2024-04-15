package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.entity.*;
import com.labmate.riddlebox.enumpackage.*;
import com.labmate.riddlebox.repository.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.labmate.riddlebox.entity.QComment.comment;
import static com.labmate.riddlebox.entity.QGame.game;
import static com.labmate.riddlebox.entity.QGameContent.gameContent;
import static com.labmate.riddlebox.entity.QGameImage.gameImage;
import static com.labmate.riddlebox.entity.QGameSolver.gameSolver;
import static com.labmate.riddlebox.entity.QGameText.gameText;
import static com.labmate.riddlebox.entity.QRBUser.rBUser;
import static com.labmate.riddlebox.enumpackage.ImageType.ILLUSTRATION;

@Service
public class TimeLimitGameService {

    @PersistenceContext
    private EntityManager entityManager;
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

    private final Logger logger = LoggerFactory.getLogger(TimeLimitGameService.class);

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
    @Transactional(readOnly = true)
    public TimeLimitGameDto getTimeLimitGameDto_QueryDslAndFetchJoin(Long gameId) {
        // 기본 게임 데이터와 일부 관련 엔티티를 가져오지만, 문제가 되는 컬렉션은 제외
        Game game = queryFactory.selectFrom(QGame.game)
                .leftJoin(QGame.game.gameText, QGameText.gameText).fetchJoin()
                .leftJoin(QGame.game.gameSolver, QGameSolver.gameSolver).fetchJoin()
                .leftJoin(QGameSolver.gameSolver.user, QRBUser.rBUser).fetchJoin()
                .where(QGame.game.id.eq(gameId))
                .fetchOne();

        // 게임 이미지 및 콘텐츠는 별도의 쿼리로 가져옴
        GameImage gameImages = queryFactory
                .selectFrom(QGameImage.gameImage)
                .where(QGameImage.gameImage.game.id.eq(gameId), gameImage.imageType.eq(ILLUSTRATION), gameImage.status.eq(GameStatus.ACTIVE))
                .fetchFirst();

        GameContent gameContents = queryFactory
                .selectFrom(QGameContent.gameContent)
                .where(QGameContent.gameContent.game.id.eq(gameId), gameContent.status.eq(GameStatus.ACTIVE))
                .fetchFirst();

        List<Comment> comments = queryFactory
                .selectFrom(QComment.comment)
                .leftJoin(QComment.comment.user, QRBUser.rBUser).fetchJoin()
                .where(QComment.comment.game.id.eq(gameId), QComment.comment.status.eq(GameStatus.ACTIVE))
                .orderBy(QComment.comment.createdDate.desc())
                .fetch();

        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> new CommentDto(
                        comment.getCommentId(),
                        comment.getUser().getId(),
                        comment.getUser().getNickname(),
                        comment.getContent()))
                .collect(Collectors.toList());


        String nickname = game.getGameSolver().getUser() != null ? game.getGameSolver().getUser().getNickname() : null;

        return new TimeLimitGameDto(
                game.getId(),
                game.getTitle(),
                gameImages.getFilePath(),
                game.getGameText().getText(),
                gameContents.getAnswer(),
                game.getGameSolver().getEndDateTime(),
                commentDtos,
                nickname
        );
    }

    //[3]차 게임 데이터 캐싱
    @Transactional(readOnly = true)
    public TimeLimitGameDto getTimeLimitGameDto_caching(Long gameId) {
        GameDataDto getGameData = getGameData(gameId);
        List<CommentDto> commentDtoList = getGameComments(gameId);

        return new TimeLimitGameDto(getGameData, commentDtoList);
    }

    @Cacheable(value = "gameDataCache", key = "#gameId")
    @Transactional(readOnly = true)
    public GameDataDto getGameData(Long gameId) {
        // 기본 게임 데이터와 일부 관련 엔티티를 가져오지만, 문제가 되는 컬렉션은 제외
        Game game = queryFactory.selectFrom(QGame.game)
                .leftJoin(QGame.game.gameText, QGameText.gameText).fetchJoin()
                .leftJoin(QGame.game.gameSolver, QGameSolver.gameSolver).fetchJoin()
                .leftJoin(QGameSolver.gameSolver.user, QRBUser.rBUser).fetchJoin()
                .where(QGame.game.id.eq(gameId))
                .fetchOne();

        // 게임 이미지 및 콘텐츠는 별도의 쿼리로 가져옴
        GameImage gameImages = queryFactory
                .selectFrom(QGameImage.gameImage)
                .where(QGameImage.gameImage.game.id.eq(gameId), gameImage.imageType.eq(ILLUSTRATION), gameImage.status.eq(GameStatus.ACTIVE))
                .fetchFirst();

        GameContent gameContents = queryFactory
                .selectFrom(QGameContent.gameContent)
                .where(QGameContent.gameContent.game.id.eq(gameId), gameContent.status.eq(GameStatus.ACTIVE))
                .fetchFirst();

        String nickname = game.getGameSolver().getUser() != null ? game.getGameSolver().getUser().getNickname() : null;

        return new GameDataDto(game.getId(),
                game.getTitle(),
                gameImages.getFilePath(),
                game.getGameText().getText(),
                gameContents.getAnswer(),
                game.getGameSolver().getEndDateTime(),
                nickname);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getGameComments(Long gameId) {
        // 댓글 데이터 조회
        List<Comment> comments = queryFactory
                .selectFrom(QComment.comment)
                .leftJoin(QComment.comment.user, QRBUser.rBUser).fetchJoin()
                .where(QComment.comment.game.id.eq(gameId), QComment.comment.status.eq(GameStatus.ACTIVE))
                .orderBy(QComment.comment.createdDate.desc())
                .fetch();

        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> new CommentDto(
                        comment.getCommentId(),
                        comment.getUser().getId(),
                        comment.getUser().getNickname(),
                        comment.getContent()))
                .collect(Collectors.toList());


        return commentDtos;
    }


    //[4]차 댓글 페이징
    @Transactional(readOnly = true)
    public TimeLimitGameDto getTimeLimitGameDto_commentPaging(Long gameId, Pageable pageable) {
        GameDataDto getGameData = getGameData(gameId);
        Page<CommentDto> commentPage = getGameCommentsPaging(gameId, pageable);

        return new TimeLimitGameDto(getGameData, commentPage.getContent(), commentPage.getNumber(), commentPage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public Page<CommentDto> getGameCommentsPaging(Long gameId, Pageable pageable) {
        List<Comment> comments = queryFactory
            .selectFrom(QComment.comment)
            .leftJoin(QComment.comment.user, QRBUser.rBUser).fetchJoin()
            .where(QComment.comment.game.id.eq(gameId), comment.status.eq(GameStatus.ACTIVE))  // status 대신 is_active를 사용
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(QComment.comment.createdDate.desc())
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
                .where(QComment.comment.game.id.eq(gameId), comment.status.eq(GameStatus.ACTIVE))
                .fetchOne();

        return new PageImpl<>(commentDtos, pageable, count);
    }




    //[5]차 댓글 테이블 인덱스 생성 및 이용
    @Transactional(readOnly = true)
    public TimeLimitGameDto getTimeLimitGameDto_commentUseIndex(Long gameId, Pageable pageable) {
        GameDataDto getGameData = getGameData(gameId);
        Page<CommentDto> commentPage = getGameCommentsUseIndex(gameId, pageable);

        return new TimeLimitGameDto(getGameData, commentPage.getContent(), commentPage.getNumber(), commentPage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public Page<CommentDto> getGameCommentsUseIndex(Long gameId, Pageable pageable) {
        List<Comment> comments = queryFactory
            .selectFrom(QComment.comment)
            .leftJoin(QComment.comment.user, QRBUser.rBUser).fetchJoin()
            .where(QComment.comment.game.id.eq(gameId), comment.isActive.eq(true))  // status 대신 is_active를 사용
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(QComment.comment.createdDate.desc())
            .fetch();


        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> new CommentDto(
                        comment.getCommentId(),
                        comment.getUser().getId(),
                        comment.getUser().getNickname(),
                        comment.getContent()))
                .collect(Collectors.toList());


//        String sql = "SELECT COUNT(comment_id) FROM comment USE INDEX (idx_game_id_is_active) WHERE game_id = ?1 AND is_active = 1";
//        Query nativeQuery = entityManager.createNativeQuery(sql);
//        nativeQuery.setParameter(1, gameId);
//        long count = ((Number) nativeQuery.getSingleResult()).longValue();

        long count = queryFactory
                .select(comment.commentId.count())
                .from(QComment.comment)
                .where(QComment.comment.game.id.eq(gameId), comment.isActive.eq(true))
                .fetchOne();

        return new PageImpl<>(commentDtos, pageable, count);
    }

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