package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.FaqCategory;
import com.labmate.riddlebox.enumpackage.InquiryStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;  //문의번호

    // 문의자 - 사용자 역할
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquirer_id")
    private RBUser inquirer;


    @Enumerated(EnumType.STRING)
    private FaqCategory faqCategory;  //이용문의,계정문의,게임컨텐츠문의,일반문의

    private String question;  //질문
    private String content;  //내용
    private LocalDateTime inquiryAt;  //문의일

    @Enumerated(EnumType.STRING)
    private InquiryStatus status;  //답변중,답변완료,임시삭제,삭제

    private String response;  //관리자 답변

    // 답변자 - 관리자 역할
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responder_id")
    private RBUser responder;

    private LocalDateTime respondedDate;  //답변일


    /*   생성자   */
    public Inquiry(RBUser inquirer, FaqCategory faqCategory, String question, String content,
                   LocalDateTime inquiryAt, InquiryStatus status,
                   String response, RBUser responder, LocalDateTime respondedDate) {

        this.inquirer = inquirer;
        this.faqCategory = faqCategory;
        this.question = question;
        this.content = content;
        this.inquiryAt = inquiryAt;
        this.status = status;
        this.response = response;
        this.responder = responder;
        this.respondedDate = respondedDate;
    }


    // RBUser 엔티티와의 관계를 설정하는 메서드
    public void setInquirer(RBUser inquirer) {
        if (this.inquirer != null) {
            this.inquirer.getInquiries().remove(this);
        }
        this.inquirer = inquirer;
    }

    public void setResponder(RBUser responder) {
        if (this.responder != null) {
            this.responder.getResponses().remove(this);
        }
        this.responder = responder;
    }



    /*    변경 메서드    */
    //문의 상태 변경하기
    public void changeStatus(InquiryStatus newStatus) {
        this.status = newStatus;
    }

    //관리자 답변
    public void addResponse(String response, RBUser responder) {
        this.response = response;
        this.respondedDate = LocalDateTime.now();
        this.responder = responder;
        changeStatus(InquiryStatus.COMPLETED);
    }

    //사용자 문의 내용 수정
    public void updateInquiry(FaqCategory newFaqCategory, String newQuestion,
                              String newContent) {
        //답변 완료된 질문 내용은 수정할 수 없음
        if (status != InquiryStatus.COMPLETED) {
            this.faqCategory = newFaqCategory;
            this.question = newQuestion;
            this.content = newContent;
        }
    }

    //삭제
    public void softDelete() {
        changeStatus(InquiryStatus.DELETED);
    }



}
