package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.MyPageDto;
import com.labmate.riddlebox.dto.MyPageProfileDto;
import com.labmate.riddlebox.dto.MyPointDto;
import com.labmate.riddlebox.dto.MyRecordDto;
import com.labmate.riddlebox.entity.*;
import com.labmate.riddlebox.entity.QGame;
import com.labmate.riddlebox.entity.QGameRecord;
import com.labmate.riddlebox.entity.QRBUser;
import com.labmate.riddlebox.entity.QUserPoint;
import com.labmate.riddlebox.enumpackage.GameResultType;
import com.labmate.riddlebox.repository.InquiryRepository;
import com.labmate.riddlebox.repository.UserRepository;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;


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

        Integer userPoint = queryFactory.select(QUserPoint.userPoint.totalPoints)
                                        .from(QUserPoint.userPoint)
                                        .where(QUserPoint.userPoint.user.id.eq(userId))
                                        .orderBy(QUserPoint.userPoint.earnedDate.desc())
                                        .fetchFirst();

        long solvedCount = queryFactory.select(QGameRecord.gameRecord.game.id.count())
                                        .from(QGameRecord.gameRecord)
                                        .distinct()
                                        .where(
                                              QGameRecord.gameRecord.user.id.eq(userId),
                                              QGameRecord.gameRecord.resultType.eq(GameResultType.SOLVED)
                                        )
                                        .fetchOne();

        long totalRecordCount = queryFactory.select(QGameRecord.gameRecord.game.id.count())
                                            .from(QGameRecord.gameRecord)
                                            .distinct()
                                            .where(
                                                  QGameRecord.gameRecord.user.id.eq(userId)
                                            )
                                            .fetchOne();


        return new MyPageDto(user.getId(), user.getNickname(), userPoint, solvedCount + "/" + totalRecordCount, null);
    }


    @Transactional(readOnly = true)
    @Override
    public Page<MyRecordDto> getUserRecordDtoList(Long userId, Pageable pageable) {

    // 결과 데이터 조회
    List<MyRecordDto> content = queryFactory
            .select(Projections.constructor(MyRecordDto.class,
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
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 총 개수 조회
        long total = queryFactory
                .select(QGameRecord.gameRecord.count())
                .from(QGameRecord.gameRecord)
                .join(QGameRecord.gameRecord.game, QGame.game)
                .where(QGameRecord.gameRecord.user.id.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Transactional(readOnly = true)
    @Override
    public MyPageProfileDto getMyPageProfileDto(Long userId) {
        RBUser user = queryFactory.selectFrom(QRBUser.rBUser).where(QRBUser.rBUser.id.eq(userId)).fetchOne();
        return new MyPageProfileDto(user.getId(), user.getLoginEmail(), user.getName(), user.getNickname(), user.getRegDate());
    }


    /*point-포인트내역*/
    @Override
    /*profile-프로필*/
    @Transactional(readOnly = true)
    public List<MyPointDto> getUserPointDtoList(Long userId) {
        return queryFactory.select(Projections.constructor(MyPointDto.class,
                                        QUserPoint.userPoint.id,
                                        QUserPoint.userPoint.reason,
                                        QUserPoint.userPoint.earnedPoints,
                                        QUserPoint.userPoint.earnedDate,
                                        QUserPoint.userPoint.totalPoints))
                                .from(QUserPoint.userPoint)
                                .where(QUserPoint.userPoint.user.id.eq(userId))
                                .fetch();
    }


    /*record-게임기록*/
    /*ranking-랭킹보기*/

    /*iquiry-문의내역*/


}
