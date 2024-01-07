package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.entity.Admin;
import com.labmate.riddlebox.entity.Member;
import com.labmate.riddlebox.enumpackage.FaqCategory;
import com.labmate.riddlebox.enumpackage.InquiryStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InquiryListDto {

    private Long id;  //문의번호
    private Member member;  //회원번호
    private FaqCategory faqCategory;  //(Enum)이용문의,계정문의,게임컨텐츠문의,일반문의
    private String question;  //질문
    private LocalDateTime inquiryAt;  //문의일
    private InquiryStatus status;  //(Enum)답변중,답변완료,임시삭제,삭제

}
