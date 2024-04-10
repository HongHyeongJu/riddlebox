package com.labmate.riddlebox.admindto;

import com.labmate.riddlebox.enumpackage.FaqCategory;
import com.labmate.riddlebox.enumpackage.NoticeStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FaqListDto {

    private Long id;  //FAQ 번호
    private FaqCategory faqCategory;  //카테고리(Enum)
    private String question;  //제목

}


