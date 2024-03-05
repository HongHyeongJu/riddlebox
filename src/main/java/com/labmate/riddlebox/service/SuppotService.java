package com.labmate.riddlebox.service;

import com.labmate.riddlebox.admindto.Question;
import com.labmate.riddlebox.dto.FaqViewDto;

import java.util.List;

public interface SuppotService {
    List<FaqViewDto> getFaqList(Long faqId);
}
