package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.enumpackage.FaqCategory;
import com.labmate.riddlebox.enumpackage.InquiryStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class FaqViewDto {

    private Long faqId;  //문의자 PK
    private String question;
    private String answer;
    private FaqCategory faqCategory;

    public FaqViewDto(Long faqId, String question, String answer, FaqCategory faqCategory) {
        this.faqId = faqId;
        this.question = question;
        this.answer = answer;
        this.faqCategory = faqCategory;
    }
}
