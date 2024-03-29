package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.dto.QGameListDto;
import com.labmate.riddlebox.entity.QGame;
import com.labmate.riddlebox.entity.QGameCategory;
import com.labmate.riddlebox.entity.QGameImage;
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
            .leftJoin(game.gameImages, gameImage).on(gameImage.fileType.eq(ImageType.THUMBNAIL))
            .where(game.title.containsIgnoreCase(keyword)
                   .or(game.description.containsIgnoreCase(keyword))
                   .or(game.gameCategory.subject.stringValue().containsIgnoreCase(keyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(game)
            .leftJoin(game.gameImages, gameImage).on(gameImage.fileType.eq(ImageType.THUMBNAIL))
            .where(game.title.containsIgnoreCase(keyword)
                   .or(game.description.containsIgnoreCase(keyword))
                   .or(game.gameCategory.subject.stringValue().containsIgnoreCase(keyword)))
            .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    public Page<GameListDto> searchByCategory(String category, Pageable pageable) {
        List<GameListDto> results = queryFactory
            .select(new QGameListDto(
                        game.id,
                        game.gameCategory,
                        game.title,
                        game.gameLevel,
                        gameImage.fileUrl
            ))
            .from(game)
            .leftJoin(game.gameImages, gameImage).on(gameImage.fileType.eq(ImageType.THUMBNAIL))
            .where(game.gameCategory.subject.stringValue().containsIgnoreCase(category))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(game)
            .leftJoin(game.gameImages, gameImage).on(gameImage.fileType.eq(ImageType.THUMBNAIL))
            .where(game.gameCategory.subject.stringValue().containsIgnoreCase(category))
            .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }



}
