package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.entity.GameCategory;
import com.labmate.riddlebox.entity.GameContent;
import com.labmate.riddlebox.entity.GameImage;
import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.GameStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class GameListDto {

    private Long id;  //게임 번호
    private GameCategory gameCategory;  //게임 카테고리
    private String title;  //제목
    private GameLevel gameLevel;  //게임 난이도
    private String thumbnailUrl; // 게임 썸네일


}
