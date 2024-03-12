package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.entity.*;
import com.labmate.riddlebox.enumpackage.FaqCategory;
import com.labmate.riddlebox.enumpackage.NoticeStatus;
import com.labmate.riddlebox.repository.FaqRepository;
import com.labmate.riddlebox.repository.NoticeRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.labmate.riddlebox.entity.QFaq.faq;

@Service
//@RequiredArgsConstructor
public class SupportServiceImpl implements SuppotService {

    private final JPAQueryFactory queryFactory;
    private final FaqRepository faqRepository;
    private final NoticeRepository noticeRepository;

    @Autowired
    public SupportServiceImpl(EntityManager em, FaqRepository faqRepository, NoticeRepository noticeRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.faqRepository = faqRepository;
        this.noticeRepository = noticeRepository;
    }


    private BooleanExpression faqIsPosted() {
        return faq.status.eq(NoticeStatus.POSTED);
    }


    @Transactional
    @Override
    public List<FaqViewDto> getFaqList(String faqKeyword) {
        // 키워드를 FaqCategory Enum으로 변환하기
        FaqCategory category = FaqCategory.fromKeyword(faqKeyword);

        BooleanExpression categoryPredicate = category != null ? QFaq.faq.faqCategory.eq(category) : null;

        BooleanExpression finalPredicate = categoryPredicate != null ? categoryPredicate.and(faqIsPosted()) : faqIsPosted();

        List<Faq> faqList = queryFactory
                .selectFrom(QFaq.faq)
                .where(finalPredicate)
                .orderBy(QFaq.faq.id.asc())
                .fetch();

        faqList.forEach(faq -> faq.addViewCount());

        return faqList.stream()
                .map(faq -> new FaqViewDto(faq.getId(), faq.getQuestion(), faq.getAnswer(), faq.getFaqCategory()))
                .collect(Collectors.toList());
    }


    @Override
    public NoticeViewDto findNoticeViewDtoById(Long noticeId) {
        QNotice notice = QNotice.notice;

        NoticeViewDto result = queryFactory
                .select(Projections.constructor(NoticeViewDto.class,
                        notice.id,
                        notice.category,
                        notice.title,
                        notice.content,
                        notice.status,
                        notice.noticeDate,
                        notice.viewCount))
                .from(notice)
                .where(notice.id.eq(noticeId))
                .fetchOne();

        return result;
    }

    @Override
    public Page<NoticeListDto> getNoticeListPaging(int page, int size) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("noticeDate"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(sorts));
        Page<Notice> noticePage = noticeRepository.findAll(pageable);
        return noticePage.map(notice -> new NoticeListDto(notice.getId(), notice.getCategory(), notice.getTitle()));
    }


    @Override
    public List<NoticeListDto> getNoticeList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("noticeDate").descending());
        Page<Notice> noticePage = noticeRepository.findByStatus(NoticeStatus.POSTED, pageable);

        return noticePage.stream()
                .map(notice -> new NoticeListDto(notice.getId(), notice.getCategory(), notice.getTitle()))
                .collect(Collectors.toList());
    }



}