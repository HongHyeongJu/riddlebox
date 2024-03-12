package com.labmate.riddlebox.service;

import com.labmate.riddlebox.admindto.Question;
import com.labmate.riddlebox.dto.FaqViewDto;
import com.labmate.riddlebox.dto.NoticeListDto;
import com.labmate.riddlebox.dto.NoticeViewDto;
import com.labmate.riddlebox.enumpackage.NoticeStatus;

import java.util.List;

public interface SuppotService {
    public List<FaqViewDto> getFaqList(String faqKeyword);

    List<NoticeListDto> getNoticeList(int page, int size);

    NoticeViewDto findNoticeViewDtoById(Long noticeId);
}
