package com.labmate.riddlebox.dto;


import lombok.*;

import java.time.LocalDateTime;

/*공지사항, 1:1문의 FAQ 목록페이지에 공통으로 사용할 Dto*/
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CommonListDto {

    private Long id; // 글 번호
    private String category; //카테고리
    private String title; // 제목
    private Long viewCount; // 조회수
    private LocalDateTime regDate; //등록일

}
