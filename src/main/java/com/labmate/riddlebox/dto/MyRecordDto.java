package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.entity.GameCategory;
import com.labmate.riddlebox.enumpackage.GameResultType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MyRecordDto {
    private Long userId;
    private Long GameId;
    private GameCategory gameCategory;  //게임 카테고리
    private String title;  //제목
    private int score;  //점수
    private float successRate;  //정답률
    private GameResultType resultType;

    public MyRecordDto(Long userId, Long gameId, GameCategory gameCategory, String title, int score, float successRate, GameResultType resultType) {
        this.userId = userId;
        GameId = gameId;
        this.gameCategory = gameCategory;
        this.title = title;
        this.score = score;
        this.successRate = successRate;
        this.resultType = resultType;
    }
}
