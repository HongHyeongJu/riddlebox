package com.labmate.riddlebox.admindto;

import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.enumpackage.FaqCategory;
import com.labmate.riddlebox.enumpackage.InquiryStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InquiryDto {

    private Long id;  //문의번호
    private RBUser inquirer;  //문의자 PK
    private String inquirerNickname;  //문의자 닉네임
    private FaqCategory faqCategory;  //(Enum)이용문의,계정문의,게임컨텐츠문의,일반문의
    private String question;  //질문
    private String content;  //내용
    private LocalDateTime inquiryAt;  //문의일
    private InquiryStatus status;  //(Enum)답변중,답변완료,임시삭제,삭제
    private String response;  //관리자 답변
    private RBUser responder;  //답변자 PK
    private String responderNickname;  //답변한 관리자 닉네임
    private LocalDateTime respondedDate;  //답변일

    private Long preInquiryId;  //이전 문의번호
    private Long nextInquiryId;  //이전 문의번호


}
