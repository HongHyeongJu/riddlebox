package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.entity.GameCategory;
import com.labmate.riddlebox.entity.GameContent;
import com.labmate.riddlebox.entity.GameImage;
import com.labmate.riddlebox.entity.QGame;
import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.GameStatus;
import com.labmate.riddlebox.repository.GameRepository;
import com.querydsl.core.QueryResults;
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
import java.util.List;

import static com.labmate.riddlebox.entity.QGame.game;
import static com.labmate.riddlebox.entity.QGameContent.gameContent;
import static com.labmate.riddlebox.entity.QGameImage.gameImage;

@Service
public class GameServiceImpl implements GameService  {

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
        if (gameTitle == null || gameTitle.isEmpty()) {return null;}
        return QGame.game.title.containsIgnoreCase(gameTitle);
    }
    private BooleanExpression descriptionContains(String gameDescription) {
        if (gameDescription == null || gameDescription.isEmpty()) {return null;}
        return QGame.game.description.containsIgnoreCase(gameDescription);
    }
    private BooleanExpression gameCategoryEquals(GameCategory gameCategory) {
        if (gameCategory == null) {return null;}
        return QGame.game.gameCategory.eq(gameCategory);
    }
    private BooleanExpression gameLevelEquals(GameLevel gameLevel) {
        if (gameLevel == null) {return null;}
        return QGame.game.gameLevel.eq(gameLevel);
    }
    private BooleanExpression officialReleaseDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {return null;}
        if (startDate != null && endDate == null) {
            return QGame.game.officialReleaseDate.after(startDate).or(QGame.game.officialReleaseDate.eq(startDate));
        }
        if (startDate == null) {
            return QGame.game.officialReleaseDate.before(endDate).or(QGame.game.officialReleaseDate.eq(endDate));
        }
        return QGame.game.officialReleaseDate.between(startDate, endDate);
    }


    //단건 게임 데이터 전달
    //게임 식별자로 게임, 게임컨텐츠, 게임이미지 조회해서 DTO 반환
    @Transactional(readOnly = true)
    public GameplayInfoDto findGameInfos(Long gameId){
        GameplayInfoDto gameplayInfoDto = getGameplayInfoDto(gameId);
        if (gameplayInfoDto == null) {
            throw new EntityNotFoundException("Game not found with ID: " + gameId);
        }

        gameplayInfoDto.setGameContents(getGameContents(gameId));
        gameplayInfoDto.setGameImages(getGameImages(gameId));

        return gameplayInfoDto;
    }


    private GameplayInfoDto getGameplayInfoDto(Long gameId) {
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
    public Page<GameListDto> searchGameSimple(GameSearchCondition condition, Pageable pageable){
        List<GameListDto> results = queryFactory.select(new QGameListDto(game.id,game.gameCategory, game.title, game.gameLevel, gameImage.fileUrl))
                                                .from(game)
                                                .leftJoin(game.gameImages, gameImage)
                                                .where(
                                                        titleContains(condition.getTitle()),
                                                        descriptionContains(condition.getDescription()),
                                                        gameCategoryEquals(condition.getGameCategory()),
                                                        gameLevelEquals(condition.getGameLevel()),
                                                        officialReleaseDateBetween(condition.getOfficialReleaseStartDate(),condition.getOfficialReleaseEndDate())
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
                                        officialReleaseDateBetween(condition.getOfficialReleaseStartDate(),condition.getOfficialReleaseEndDate())
                                 )
                                 .fetchCount();

        return new PageImpl<>(results, pageable, total);
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
