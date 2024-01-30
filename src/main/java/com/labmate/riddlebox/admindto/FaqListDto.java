package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.enumpackage.FaqCategory;
import com.labmate.riddlebox.enumpackage.NoticeStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FaqListDto {

    private Long id;  //공지사항번호
    private FaqCategory faqCategory;  //카테고리(Enum)
    private String question;  //제목

}


