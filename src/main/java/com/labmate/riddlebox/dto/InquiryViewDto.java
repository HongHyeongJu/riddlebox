package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.enumpackage.FaqCategory;
import com.labmate.riddlebox.enumpackage.InquiryStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class InquiryViewDto {

    private Long inquirerId;  //문의자 PK
    private String inquirerNickName;  //문의자닉네임
    private FaqCategory faqCategory;
    private String question;
    private String content;
    private LocalDateTime inquiryAt;
    private InquiryStatus status;  //답변중,답변완료,임시삭제,삭제
    private String response;  //관리자 답변
    private Long responderId;  //관리자 PK
    private String responderNickName; //관리자닉네임
    private LocalDateTime respondedDate;  //답변일

    public InquiryViewDto(Long inquirerId, String inquirerNickName, FaqCategory faqCategory,
                          String question, String content, LocalDateTime inquiryAt,
                          InquiryStatus status, String response, Long responderId, String responderNickName,
                          LocalDateTime respondedDate) {
        this.inquirerId = inquirerId;
        this.inquirerNickName = inquirerNickName;
        this.faqCategory = faqCategory;
        this.question = question;
        this.content = content;
        this.inquiryAt = inquiryAt;
        this.status = status;
        this.response = response;
        this.responderId = responderId;
        this.responderNickName = responderNickName;
        this.respondedDate = respondedDate;
    }
}
