package com.labmate.riddlebox.service;

import com.labmate.riddlebox.admindto.Question;
import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.entity.*;
import com.labmate.riddlebox.enumpackage.GameStatus;
import com.labmate.riddlebox.enumpackage.NoticeStatus;
import com.labmate.riddlebox.repository.FaqRepository;
import com.labmate.riddlebox.repository.NoticeRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.labmate.riddlebox.entity.QFaq.faq;
import static com.labmate.riddlebox.entity.QGame.game;

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


    private BooleanExpression gameIsActive() {
        return faq.status.eq(NoticeStatus.POSTED);
    }

    /*게시된 Faq List 가져오기*/
    @Override
    public List<FaqViewDto> getFaqList(Long faqId) {
        List<Faq> faqList = queryFactory
                                        .selectFrom(faq)
                                        .where(gameIsActive())
                                        .orderBy(faq.id.asc()) // ordering 값으로 정렬
                                        .fetch();

        List<FaqViewDto> faqViewDtos = new ArrayList<>();
        for (Faq faq : faqList) {
            FaqViewDto faqViewDto = new FaqViewDto(faq.getId(), faq.getQuestion(), faq.getAnswer(), faq.getFaqCategory());
            faqViewDtos.add(faqViewDto);
        }

        return faqViewDtos;
    }


    /*페이징된 공지사항 목록 가져오기 5개!*/




}