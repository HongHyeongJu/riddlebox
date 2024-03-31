package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.entity.GameCategory;
import com.labmate.riddlebox.entity.GameContent;
import com.labmate.riddlebox.entity.GameImage;
import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.GameStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GameplayInfoDto {

    private Long id;  //게임 번호

    private GameCategory gameCategory;  //게임 카테고리

    private String title;  //제목
    private String description;  //설명

    private GameLevel gameLevel;  //게임 난이도

    private GameStatus status;  //상태

    private int viewCount;  //조회수

    private String author;  //작가
    private LocalDateTime officialReleaseDate;  //게임생성일
    private LocalDateTime officialUpdateDate;  //게임수정일

    private List<GameContent> gameContents = new ArrayList<>();
    private String thumnailImgPath;
    private String illustrationImgPath;

    @QueryProjection
    public GameplayInfoDto(Long id, GameCategory gameCategory,
                           String title, String description,
                           GameLevel gameLevel, GameStatus status,
                           int viewCount, String author,
                           LocalDateTime officialReleaseDate, LocalDateTime officialUpdateDate) {
        this.id = id;
        this.gameCategory = gameCategory;
        this.title = title;
        this.description = description;
        this.gameLevel = gameLevel;
        this.status = status;
        this.viewCount = viewCount;
        this.author = author;
        this.officialReleaseDate = officialReleaseDate;
        this.officialUpdateDate = officialUpdateDate;
    }


}
