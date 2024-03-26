package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.MyPageDto;
import com.labmate.riddlebox.dto.MyPageProfileDto;
import com.labmate.riddlebox.dto.MyRecordDto;
import com.labmate.riddlebox.entity.*;
import com.labmate.riddlebox.enumpackage.GameResultType;
import com.labmate.riddlebox.repository.InquiryRepository;
import com.labmate.riddlebox.repository.UserRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.List;

import static com.labmate.riddlebox.entity.QRBUser.rBUser;

@Service
public class MyPageServiceImpl implements MyPageService {

    private final JPAQueryFactory queryFactory;
    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;

    @Autowired
    public MyPageServiceImpl(EntityManager em, UserRepository userRepository,
                             InquiryRepository inquiryRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.userRepository = userRepository;
        this.inquiryRepository = inquiryRepository;

    }

    @Transactional
    @Override
    /*profile-프로필*/
    public MyPageDto getUserMyPageDto(long userId) {

        RBUser user = queryFactory.selectFrom(rBUser).where(rBUser.id.eq(userId)).fetchOne();

        Integer userPoint = queryFactory.select(QUserPoint.userPoint.total_points)
                                        .from(QUserPoint.userPoint)
                                        .where(QUserPoint.userPoint.user.id.eq(userId))
                                        .orderBy(QUserPoint.userPoint.earned_date.desc())
                                        .fetchFirst();

        long solvedCount = queryFactory.select(QGameRecord.gameRecord.game.id)
                                        .from(QGameRecord.gameRecord)
                                        .distinct()
                                        .where(
                                              QGameRecord.gameRecord.user.id.eq(userId),
                                              QGameRecord.gameRecord.resultType.eq(GameResultType.SOLVED)
                                        )
                                        .fetchCount();

        long totalRecordCount = queryFactory.select(QGameRecord.gameRecord.game.id)
                                            .from(QGameRecord.gameRecord)
                                            .distinct()
                                            .where(
                                                  QGameRecord.gameRecord.user.id.eq(userId)
                                            )
                                            .fetchCount();


        return new MyPageDto(user.getId(), user.getNickname(), userPoint, solvedCount + "/" + totalRecordCount, null);
    }


    @Override
    /*profile-프로필*/
    public List<MyRecordDto> getUserRecordDtoList(Long userId) {
        return queryFactory.select(Projections.constructor(MyRecordDto.class,
                        QGameRecord.gameRecord.user.id,
                        QGameRecord.gameRecord.game.id,
                        QGame.game.gameCategory,
                        QGame.game.title,
                        QGameRecord.gameRecord.score,
                        QGameRecord.gameRecord.successRate,
                        QGameRecord.gameRecord.resultType))
                .from(QGameRecord.gameRecord)
                .join(QGameRecord.gameRecord.game, QGame.game)
                .where(QGameRecord.gameRecord.user.id.eq(userId))
                .fetch();
    }


    @Override
    public MyPageProfileDto getMyPageProfileDto(Long userId) {
        RBUser user = queryFactory.selectFrom(QRBUser.rBUser).where(QRBUser.rBUser.id.eq(userId)).fetchOne();
        return new MyPageProfileDto(user.getId(), user.getLoginEmail(), user.getName(), user.getNickname(), user.getRegDate());
    }

    /*record-게임기록*/
    /*ranking-랭킹보기*/
    /*point-포인트내역*/
    /*iquiry-문의내역*/


}
