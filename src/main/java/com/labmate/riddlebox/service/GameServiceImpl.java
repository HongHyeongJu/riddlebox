package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.dto.QGameListDto;
import com.labmate.riddlebox.dto.QGameplayInfoDto;
import com.labmate.riddlebox.entity.GameCategory;
import com.labmate.riddlebox.entity.GameContent;
import com.labmate.riddlebox.entity.GameImage;
import com.labmate.riddlebox.entity.QGame;
import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.GameStatus;
import com.labmate.riddlebox.repository.GameRepository;
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
import java.util.stream.Collectors;

import static com.labmate.riddlebox.entity.QGame.game;
import static com.labmate.riddlebox.entity.QGameContent.gameContent;
import static com.labmate.riddlebox.entity.QGameImage.gameImage;
import static com.labmate.riddlebox.entity.QRecommendGame.recommendGame;

@Service
//@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final JPAQueryFactory queryFactory;
    private final GameRepository gameRepository;

    @Autowired
    public GameServiceImpl(EntityManager em, GameRepository gameRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.gameRepository = gameRepository;
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
    @Transactional(readOnly = true)
    public GameplayInfoDto findGameInfo(Long gameId) {
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


    @Transactional(readOnly = true)
    public GameplayInfoDto getGameplayInfoDto(Long gameId) {
        return queryFactory
                .select(new QGameplayInfoDto(game.id, game.gameCategory, game.title, game.description, game.gameLevel,
                        game.status, game.viewCount, game.author, game.officialReleaseDate, game.officialUpdateDate))
                .from(game)
                .where(gameIdEquals(gameId), gameIsActive())
                .fetchOne();
    }

    private List<GameContent> getGameContents(Long gameId) {
        return queryFactory
                .selectFrom(gameContent)
                .where(gameIdEquals(gameId), gameContentIsActive())
                .fetch();
    }

    private List<GameImage> getGameImages(Long gameId) {
        return queryFactory
                .selectFrom(gameImage)
                .where(gameIdEquals(gameId), gameImageIsActive())
                .fetch();
    }


    //게임 목록 검색(검색 조건, 페이져블)
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
        if (!isCorrect) {
            // memberId와 오답 정보를 사용하여 오답 기록
        }

        return isCorrect;
    }

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
