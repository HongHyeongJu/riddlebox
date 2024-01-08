package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.enumpackage.NoticeCategory;
import com.labmate.riddlebox.enumpackage.NoticeStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NoticeListDto {

    private Long id;  //공지사항번호
    private NoticeCategory category;  //주제
    private String title;  //제목
    private LocalDateTime noticeDate;
    private int viewCount;  //조회수

}
