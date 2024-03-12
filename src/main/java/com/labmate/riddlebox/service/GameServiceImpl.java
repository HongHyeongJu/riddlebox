package com.labmate.riddlebox.service;

import com.labmate.riddlebox.admindto.Question;
import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.dto.QGameListDto;
import com.labmate.riddlebox.dto.QGameplayInfoDto;
import com.labmate.riddlebox.entity.*;
import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.GameResultType;
import com.labmate.riddlebox.enumpackage.GameStatus;
import com.labmate.riddlebox.repository.GameRecordRepository;
import com.labmate.riddlebox.repository.GameRepository;
import com.labmate.riddlebox.repository.UserRepository;
import com.labmate.riddlebox.util.GameScoreResult;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.labmate.riddlebox.entity.QGame.game;
import static com.labmate.riddlebox.entity.QGameContent.gameContent;
import static com.labmate.riddlebox.entity.QGameImage.gameImage;
import static com.labmate.riddlebox.entity.QRecommendGame.recommendGame;

@Service
//@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final JPAQueryFactory queryFactory;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GameRecordRepository gameRecordRepository;

    @Autowired
    public GameServiceImpl(EntityManager em, GameRepository gameRepository,
                           UserRepository userRepository, GameRecordRepository gameRecordRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.gameRecordRepository = gameRecordRepository;
    }


    /*  WHERE절에 사용되는 조건  */
    private BooleanExpression gameIdEquals(Long gameId) {
        return game.id.eq(gameId);
    }

    private BooleanExpression gameIsActive() {
        return game.status.eq(GameStatus.ACTIVE);
    }

    private BooleanExpression gameContentIsActive() {
        return gameContent.status.eq(GameStatus.ACTIVE);
    }

    private BooleanExpression gameImageIsActive() {
        return gameImage.status.eq(GameStatus.ACTIVE);
    }

    private BooleanExpression titleContains(String gameTitle) {
        if (gameTitle == null || gameTitle.isEmpty()) {
            return null;
        }
        return QGame.game.title.containsIgnoreCase(gameTitle);
    }

    private BooleanExpression descriptionContains(String gameDescription) {
        if (gameDescription == null || gameDescription.isEmpty()) {
            return null;
        }
        return QGame.game.description.containsIgnoreCase(gameDescription);
    }

    private BooleanExpression gameCategoryEquals(GameCategory gameCategory) {
        if (gameCategory == null) {
            return null;
        }
        return QGame.game.gameCategory.eq(gameCategory);
    }

    private BooleanExpression gameLevelEquals(GameLevel gameLevel) {
        if (gameLevel == null) {
            return null;
        }
        return QGame.game.gameLevel.eq(gameLevel);
    }

    private BooleanExpression officialReleaseDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }
        if (startDate != null && endDate == null) {
            return QGame.game.officialReleaseDate.after(startDate).or(QGame.game.officialReleaseDate.eq(startDate));
        }
        if (startDate == null) {
            return QGame.game.officialReleaseDate.before(endDate).or(QGame.game.officialReleaseDate.eq(endDate));
        }
        return QGame.game.officialReleaseDate.between(startDate, endDate);
    }


    //단건 게임 데이터 전달
    /*게임 식별자로 게임, 게임컨텐츠, 게임이미지 조회해서 DTO 반환*/
    @Transactional
    public GameplayInfoDto findGameInfo(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        // 조회수 증가
        game.addViewCount();
        gameRepository.save(game);

        GameplayInfoDto gameplayInfoDto = getGameplayInfoDto(gameId);
        if (gameplayInfoDto == null) {
            throw new EntityNotFoundException("Game not found with ID: " + gameId);
        }

        gameplayInfoDto.setGameContents(getGameContents(gameId));
        gameplayInfoDto.setGameImages(getGameImages(gameId));

        return gameplayInfoDto;
    }


    /*각 게임의 질문리스트 반환하는 메서드
     * @param gameId
     * @return List<Question>  : Question(gameContentId, question, ordering)
     * */
    @Override
    public List<Question> getQuestionList(Long gameId) {
        List<GameContent> gameContents = queryFactory
                .selectFrom(gameContent)
                .where(gameIdEquals(gameId), gameContentIsActive())
                .orderBy(gameContent.ordering.asc()) // ordering 값으로 정렬
                .fetch();

        List<Question> questions = new ArrayList<>();
        for (GameContent content : gameContents) {
            Question question = new Question(content.getId(), content.getQuestion());
            questions.add(question);
        }

        return questions;
    }

    /*게임 관련 정보(카테고리, 게임정보, 게임컨텐츠, 게임이미지) 가져오기*/
    @Transactional(readOnly = true)
    public GameplayInfoDto getGameplayInfoDto(Long gameId) {
        return queryFactory
                .select(new QGameplayInfoDto(game.id, game.gameCategory, game.title, game.description, game.gameLevel,
                        game.status, game.viewCount, game.author, game.officialReleaseDate, game.officialUpdateDate))
                .from(game)
                .where(gameIdEquals(gameId), gameIsActive())
                .fetchOne();
    }

    /*게임 관련 컨텐츠(질문,답) 가져오기*/
    private List<GameContent> getGameContents(Long gameId) {
        return queryFactory
                .selectFrom(gameContent)
                .where(gameIdEquals(gameId), gameContentIsActive())
                .fetch();
    }

    /*게임 관련 이미지 가져오기*/
    private List<GameImage> getGameImages(Long gameId) {
        return queryFactory
                .selectFrom(gameImage)
                .where(gameIdEquals(gameId), gameImageIsActive())
                .fetch();
    }

    /*게임 목록 검색하기 (조건, 페이져블)*/
    public Page<GameListDto> searchGameSimple(GameSearchCondition condition, Pageable pageable) {
        List<GameListDto> results = queryFactory.select(new QGameListDto(game.id, game.gameCategory, game.title, game.gameLevel, gameImage.fileUrl))
                .from(game)
                .leftJoin(game.gameImages, gameImage)
                .where(
                        titleContains(condition.getTitle()),
                        descriptionContains(condition.getDescription()),
                        gameCategoryEquals(condition.getGameCategory()),
                        gameLevelEquals(condition.getGameLevel()),
                        officialReleaseDateBetween(condition.getOfficialReleaseStartDate(), condition.getOfficialReleaseEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(game.id)
                .from(game)
                .where(titleContains(condition.getTitle()),
                        descriptionContains(condition.getDescription()),
                        gameCategoryEquals(condition.getGameCategory()),
                        gameLevelEquals(condition.getGameLevel()),
                        officialReleaseDateBetween(condition.getOfficialReleaseStartDate(), condition.getOfficialReleaseEndDate())
                )
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }


    // TODO: 2024-01-17 현재는 MariaDB 이용하기. 나중에 동시성 문제 고려해서 Redis로 변경하기
    /*단건 질문 채점*/
    @Override
    public boolean checkAnswer(Long gameContentId, String userAnswer, Long memberId) {
        //UserAnswerDto: Long gameId; Long gameContentId; String userAnswer; boolean isCorrect;

        //answer의 gameContentId 꺼내서 답변 찾기 -> userAnswer 포함되었는지 확인. isCorrect에 저장
        long count = queryFactory.selectFrom(gameContent)
                .where(
                        gameContent.id.eq(gameContentId),
                        gameContent.answer.contains(userAnswer)
                )
                .fetchCount();
        boolean isCorrect = count > 0;

        // TODO: 2024-01-15 오답 기록을 위한 Table 생성하고 기록 추가하기. gameContentId, 사용자 오답, 시스템컬럼
        /* TODO: 사용자가 뒤로 가기나 새로고침을 통해 이전 페이지로 돌아갔을 때 중복된 게임 결과 기록을 방지하기
         *        사용자가 게임에 참여할 때, 해당 게임 세션에 대한 상태를 서버에 저장하기
         *        ex)예를 들어, "진행 중", "완료됨" 등의 상태를 관리해서 진행중 일때만 기록하기!!  */
        if (!isCorrect) {
            // memberId와 오답 정보를 사용하여 오답 기록
        }

        return isCorrect;
    }


    /*List 채점*/
    @Override
    public GameScoreResult checkAnswers(Map<Long, String> answers, Long memberId) {
        int correctCount = 0;
        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            Long gameContentId = entry.getKey();
            String userAnswer = entry.getValue();

            if (checkAnswer(gameContentId, userAnswer, memberId)) {
                correctCount++;
            }
        }
        return new GameScoreResult(answers.size(), correctCount);
    }


    /*홈페이지 추천 게임*/
    @Override
    public List<GameListDto> fetchRecommendedGamesForHomepage() {

        List<GameListDto> results = queryFactory
                .select(new QGameListDto(
                        game.id,
                        game.gameCategory,
                        game.title,
                        game.gameLevel,
                        gameImage.fileUrl
                ))
                .from(recommendGame)
                .innerJoin(recommendGame.game, game) // recommendGame과 game을 조인합니다.
                .leftJoin(game.gameImages, gameImage) // game과 gameImage를 조인합니다. 여러 이미지가 있는 경우 추가 처리가 필요할 수 있습니다.
                .fetch();

        return results;
    }

/*       List<GameListDto> results = queryFactory.select(new QGameListDto(game.id,game.gameCategory, game.title, game.gameLevel, gameImage.fileUrl))
                                                .from(game)
                                                .innerJoin(game.gameImages, gameImage) // game 엔티티와 gameImage 엔티티를 연결
                                                .innerJoin(game.recommendGames, recommendGame)
                                                .fetch();*/


    @Override
    // 게임 결과 기록 메서드
    @Transactional
    public void recordGameResult(Long gameId, Long userId, int totalQuestions, int correctAnswersCount, boolean isFail) {

        // 사용자와 게임 엔티티 조회
        RBUser user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new EntityNotFoundException("Game not found"));

        // 게임 기록 생성
        float successRate = ((float) correctAnswersCount / totalQuestions) * 100;
        GameResultType resultType = isFail ? GameResultType.UNSOLVED : GameResultType.SOLVED;
        GameRecord gameRecord = new GameRecord(user, game, correctAnswersCount, 0 , successRate, resultType);

        // 게임 기록 저장
        gameRecordRepository.save(gameRecord);
    }

}


//나중에 수정일 검색에 사용할 코드
/*
private BooleanExpression officialUpdateDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate == null && endDate == null) {
        return null;
    }
    if (startDate != null && endDate == null) {
        return QGame.game.officialUpdateDate.after(startDate).or(QGame.game.officialUpdateDate.eq(startDate));
    }
    if (startDate == null) {
    return QGame.game.officialUpdateDate.before(endDate).or(QGame.game.officialUpdateDate.eq(endDate));
}
return QGame.game.officialUpdateDate.between(startDate, endDate);*/
