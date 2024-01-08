package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.dto.GameSearchCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

public class GameRepositoryImpl implements GameRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public GameRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override                       //사용자용 검색
    public List<GameListDto> search(GameSearchCondition condition) {
        return null;





    }
}
