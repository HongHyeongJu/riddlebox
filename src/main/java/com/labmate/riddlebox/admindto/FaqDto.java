package com.labmate.riddlebox.admindto;

import com.labmate.riddlebox.enumpackage.FaqCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FaqDto {

    private Long id;  //공지사항번호
    private FaqCategory faqCategory;  //카테고리(Enum)
    private String question;  //제목
    private String answer;  //답변

}


