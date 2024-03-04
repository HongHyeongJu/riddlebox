package com.labmate.riddlebox.service;

import com.labmate.riddlebox.admindto.Question;
import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.entity.*;
import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.GameStatus;
import com.labmate.riddlebox.repository.FaqRepository;
import com.labmate.riddlebox.repository.GameRepository;
import com.labmate.riddlebox.repository.NoticeRepository;
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
public class SupportServiceImpl implements SuppotService {

    private final JPAQueryFactory queryFactory;
    private final FaqRepository faqRepository;
    private final NoticeRepository noticeRepository;

    @Autowired
    public SupportServiceImpl(EntityManager em, FaqRepository faqRepository,  NoticeRepository noticeRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.faqRepository = faqRepository;
        this.noticeRepository = noticeRepository;
    }





}