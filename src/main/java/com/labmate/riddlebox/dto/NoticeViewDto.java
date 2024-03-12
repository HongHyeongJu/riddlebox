package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.enumpackage.NoticeCategory;
import com.labmate.riddlebox.enumpackage.NoticeStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class NoticeViewDto {

    private Long noticeId;
    private NoticeCategory category;
    private String title;
    private String content;  //내용
    private NoticeStatus status;  //상태
    private LocalDateTime noticeDate;
    private Integer viewCount;  //조회수
//    private String preTitle; //관리자닉네임
//    private Long preNoticeId; //관리자닉네임
//    private String nextTitle; //관리자닉네임
//    private Long nextoticeId; //관리자닉네임


    public NoticeViewDto(Long noticeId, NoticeCategory category, String title, String content,
                         NoticeStatus status, LocalDateTime noticeDate, Integer viewCount) {
        this.noticeId = noticeId;
        this.category = category;
        this.title = title;
        this.content = content;
        this.status = status;
        this.noticeDate = noticeDate;
        this.viewCount = viewCount;
    }
}
