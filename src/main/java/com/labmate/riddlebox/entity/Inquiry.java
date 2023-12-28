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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  //회원번호

    @Enumerated(EnumType.STRING)
    private FaqCategory subject;  //이용문의,계정문의,게임컨텐츠문의,일반문의

    private String question;  //질문
    private String content;  //내용
    private LocalDateTime inquiryAt;  //문의일

    @Enumerated(EnumType.STRING)
    private InquiryStatus status;  //답변중,답변완료,임시삭제,삭제

    private String response;  //관리자 답변

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;  //답변한 관리자 번호

    private LocalDateTime respondedDate;  //답변일



    /*   생성자   */
    public Inquiry(Member member, FaqCategory subject, String question, String content) {
        this.member = member;
        this.subject = subject;
        this.question = question;
        this.content = content;
        this.inquiryAt = LocalDateTime.now(); // 문의 일자를 현재 시간으로 설정
        this.status = InquiryStatus.ANSWERING; // 초기 상태 설정
    }


    /*    변경 메서드    */
    //문의 상태 변경하기
    public void changeStatus(InquiryStatus newStatus) {
        this.status = newStatus;
    }

    //관리자 답변
    public void addResponse(String response, Admin responder) {
        this.response = response;
        this.respondedDate = LocalDateTime.now();
        this.admin = responder;
        changeStatus(InquiryStatus.COMPLETED);
    }

    //사용자 문의 내용 수정
    public void updateInquiry(FaqCategory newSubject, String newQuestion,
                              String newContent) {

        //답변 완료된 질문 내용은 수정할 수 없음
        if (status != InquiryStatus.COMPLETED) {
            this.subject = newSubject;
            this.question = newQuestion;
            this.content = newContent;
        }
    }

    //삭제
    public void softDelete() {
        changeStatus(InquiryStatus.DELETED);
    }



}
