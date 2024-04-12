package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.entity.GameCategory;
import com.labmate.riddlebox.entity.GameContent;
import com.labmate.riddlebox.entity.GameImage;
import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.GameStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@NoArgsConstructor
public class GameListDto {

    private Long id;  //게임 번호
    private GameCategory gameCategory;  //게임 카테고리
    private String gameUrlType;  //게임에 따른 url
    private String title;  //제목
    private GameLevel gameLevel;  //게임 난이도
    private String thumbnailFilePath; // 게임 썸네일


    //컴파일 오류를 잡을 수 있음 List<MemberDto> result = queryFactory.select(new QMemberDto(member.username, member.age)) ...
    //단점 : 큐파일 생성해야함. Dto자체가 Querydsl에 의존성 갖게됨. 서비스, 컨트롤러에서 사용하기에 깔끔하지 않음
    @QueryProjection
    public GameListDto(Long id, GameCategory gameCategory,String title, GameLevel gameLevel, String thumbnailFilePath) {
        this.id = id;
        this.gameCategory = gameCategory;
        setGameUrlType(gameCategory);
        this.title = title;
        this.gameLevel = gameLevel;
        this.thumbnailFilePath = thumbnailFilePath;
    }

    public void setGameUrlType(GameCategory gameCategory) {
        this.gameUrlType = switch (gameCategory.getSubject()) {
            case SNAPSHOT_DEDUCTION -> "snapshot";
            case SHORT_STORY -> "story";
            case EMOJI_GAME -> "emoji";
            case TIME_LIMIT -> "timelimit";
            case MYSTERY -> null;
            case ADVENTURE -> null;
            case EVERYDAY -> null;
        };
    }

}
