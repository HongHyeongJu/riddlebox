package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.entity.GameCategory;
import com.labmate.riddlebox.enumpackage.GameLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class GameSearchCondition {

    private String title;  //제목 검색
    private GameCategory gameCategory;  //게임 카테고리 검색
    private GameLevel gameLevel;  //게임 난이도 검색
    private LocalDateTime startDate; //특정 기간 내에 생성된 게임 검색
    private LocalDateTime endDate;

}
