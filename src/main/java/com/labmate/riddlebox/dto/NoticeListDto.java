package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.enumpackage.NoticeCategory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class NoticeListDto {

    private Long noticeId;
    private NoticeCategory category;
    private String title;

    public NoticeListDto(Long noticeId, NoticeCategory category, String title) {
        this.noticeId = noticeId;
        this.category = category;
        this.title = title;
    }
}
