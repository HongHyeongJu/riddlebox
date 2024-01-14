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
    private String description;  //내용 검색
    private GameCategory gameCategory;  //게임 카테고리 검색
    private GameLevel gameLevel;  //게임 난이도 검색
    private LocalDateTime officialReleaseStartDate; //정식 공개일
    private LocalDateTime officialReleaseEndDate; //정식 공개일
    private LocalDateTime officialUpdateStartDate; //공식 업데이트일
    private LocalDateTime officialUpdateEndDate; //공식 업데이트일

    public GameSearchCondition(String title, String description,
                               GameCategory gameCategory, GameLevel gameLevel,
                               LocalDateTime officialReleaseStartDate, LocalDateTime officialReleaseEndDate,
                               LocalDateTime officialUpdateStartDate, LocalDateTime officialUpdateEndDate) {
        this.title = title;
        this.description = description;
        this.gameCategory = gameCategory;
        this.gameLevel = gameLevel;
        this.officialReleaseStartDate = officialReleaseStartDate;
        this.officialReleaseEndDate = officialReleaseEndDate;
        this.officialUpdateStartDate = officialUpdateStartDate;
        this.officialUpdateEndDate = officialUpdateEndDate;
    }
}
