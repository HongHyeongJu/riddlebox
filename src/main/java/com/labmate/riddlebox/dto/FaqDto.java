package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.enumpackage.FaqCategory;
import com.labmate.riddlebox.enumpackage.NoticeStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class FaqDto {

    private Long id;  //공지사항번호
    private FaqCategory faqCategory;  //카테고리(Enum)
    private String question;  //제목
    private String answer;  //답변
    private NoticeStatus status;  //상태(Enum)
    private int viewCount;  //조회수

}


