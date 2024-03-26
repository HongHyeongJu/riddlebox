package com.labmate.riddlebox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
// 게임 중단 정보를 담는 클래스
public class GameExitRequest {
    private Long gameId;
    private int playTime;
    private int correctAnswers;
    private int incorrectAnswers;
    private String gameResult;

    public GameExitRequest(Long gameId, int playTime, int correctAnswers, int incorrectAnswers, String gameResult) {
        this.gameId = gameId;
        this.playTime = playTime;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
        this.gameResult = gameResult;
    }

    public GameExitRequest(Long gameId, int playTime, String gameResult) {
        this.gameId = gameId;
        this.playTime = playTime;
        this.gameResult = gameResult;
    }

}