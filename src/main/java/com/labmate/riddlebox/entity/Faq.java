package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.FaqCategory;
import com.labmate.riddlebox.enumpackage.NoticeStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Faq extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private Long id;  //공지사항번호

    @Enumerated(EnumType.STRING)
    private FaqCategory faqCategory;  //주제

    private String question;  //제목
    private String answer;  //답변

    @Enumerated(EnumType.STRING)
    private NoticeStatus status;  //상태

    private int viewCount;  //조회수


    /*   생성자   */
    public Faq(FaqCategory faqCategory, String question, String answer) {
        this.faqCategory = faqCategory;
        this.question = question;
        this.answer = answer;
        this.status = NoticeStatus.POSTED; // 기본 상태 설정
        this.viewCount = 0; // 조회수 초기화
    }

    /*    변경 메서드    */
    //FAQ 상태 변경하기
    public void changeStatus(NoticeStatus newStatus) {
        this.status = newStatus;
    }

    //FAQ 내용 수정
    public void updateFaq(FaqCategory newFaqCategory, String newQuestion, String newAnswer) {
        this.faqCategory = newFaqCategory;
        this.question = newQuestion;
        this.answer = newAnswer;
    }

    //삭제
    public void softDelete() {
        changeStatus(NoticeStatus.DELETED);
    }

    //조회수 증가
    public void addViewCount() {
        this.viewCount++;
    }



}
