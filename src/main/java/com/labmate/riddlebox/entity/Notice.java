package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;  //공지사항번호

    @Enumerated(EnumType.STRING)
    private NoticeCategory category;  //주제
    private String title;  //제목

    @Lob  // 긴 텍스트를 위한 애너테이션
    @Column
    private String content;  //내용

    @Enumerated(EnumType.STRING)
    private NoticeStatus status;  //상태

    private LocalDateTime noticeDate;  //notice_date
    private Integer viewCount;  //조회수



    /*   생성자   */
    public Notice(NoticeCategory category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.noticeDate = LocalDateTime.now();
        this.status = NoticeStatus.POSTED; //기본값-게시
        this.viewCount = 0; //조회수 초기화
    }


    /*    변경 메서드    */
    //공지 상태 변경하기
    public void changeStatus(NoticeStatus newStatus) {
        this.status = newStatus;
    }

    //공지내용 수정
    public void updateNotice(NoticeCategory newCategory, String newTitle, String newContent) {
        this.category = newCategory;
        this.title = newTitle;
        this.content = newContent;
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
