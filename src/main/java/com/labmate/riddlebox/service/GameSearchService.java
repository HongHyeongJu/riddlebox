package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.dto.QGameListDto;
import com.labmate.riddlebox.entity.GameCategory;
import com.labmate.riddlebox.entity.QGame;
import com.labmate.riddlebox.entity.QGameCategory;
import com.labmate.riddlebox.entity.QGameImage;
import com.labmate.riddlebox.enumpackage.GameSubject;
import com.labmate.riddlebox.enumpackage.ImageType;
import com.labmate.riddlebox.repository.GameRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.labmate.riddlebox.entity.QGame.game;
import static com.labmate.riddlebox.entity.QGameImage.gameImage;

@Service
public class GameSearchService {

    private final GameRepository gameRepository;

    private final JPAQueryFactory queryFactory;

    public GameSearchService(EntityManager em, GameRepository gameRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.gameRepository = gameRepository;
    }


    @Transactional(readOnly = true)
    public Page<GameListDto> searchByKeyword(String keyword, Pageable pageable) {

        List<GameListDto> results = queryFactory
            .select(new QGameListDto(
                        game.id,
                        game.gameCategory,
                        game.title,
                        game.gameLevel,
                        gameImage.fileUrl
            ))
            .from(game)
            .leftJoin(game.gameImages, gameImage).on(gameImage.imageType.eq(ImageType.THUMBNAIL))
            .where(game.title.like("%" + keyword + "%")
                   .or(game.descript.like("%" + keyword + "%")))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(game.count())
            .from(game)
            .leftJoin(game.gameImages, gameImage).on(gameImage.imageType.eq(ImageType.THUMBNAIL))
            .where(game.title.like("%" + keyword + "%")
                   .or(game.descript.like("%" + keyword + "%")))
            .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    @Transactional(readOnly = true)
    public Page<GameListDto> searchByCategory(String category, Pageable pageable) {
        // category 문자열을 대응되는 ENUM 값으로 변환
        GameSubject gameCategoryEnum = GameSubject.valueOf(category.toUpperCase());

        List<GameListDto> results = queryFactory
            .select(new QGameListDto(
                        game.id,
                        game.gameCategory,
                        game.title,
                        game.gameLevel,
                        gameImage.fileUrl
            ))
            .from(game)
            .leftJoin(game.gameImages, gameImage).on(gameImage.imageType.eq(ImageType.THUMBNAIL))
            .where(game.gameCategory.subject.eq(gameCategoryEnum))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(game.count()).from(game)
            .leftJoin(game.gameImages, gameImage).on(gameImage.imageType.eq(ImageType.THUMBNAIL))
            .where(game.gameCategory.subject.eq(gameCategoryEnum))
            .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }



}
