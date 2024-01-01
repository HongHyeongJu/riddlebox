package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.entity.GameCategory;
import com.labmate.riddlebox.entity.GameContent;
import com.labmate.riddlebox.entity.GameImage;
import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.GameStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PlayGameDto {

    private Long id;  //게임 번호

    private GameCategory gameCategory;  //게임 카테고리

    private String title;  //제목
    private String description;  //설명

    private GameLevel gameLevel;  //게임 난이도

    private GameStatus status;  //상태

    private int viewCount;  //조회수

    private String author;  //작가
    private LocalDateTime createDate;  //게임생성일
    private LocalDateTime updateDate;  //게임수정일

    private List<GameContent> gameContents = new ArrayList<>();
    private List<GameImage> gameImages = new ArrayList<>();


}
