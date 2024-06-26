package com.labmate.riddlebox.admindto;

import com.labmate.riddlebox.enumpackage.NoticeStatus;
import com.labmate.riddlebox.enumpackage.NoticeCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NoticeDto {

    private Long id;  //공지사항번호
    private NoticeCategory category;  //주제
    private String title;  //제목
    private String content;  //내용
    private NoticeStatus status;  //상태(Enum)
    private LocalDateTime noticeDate;
    private int viewCount;  //조회수

    private Long preNoticeId;  //이전 문의번호
    private Long nextNoticeId;  //이전 문의번호

}
