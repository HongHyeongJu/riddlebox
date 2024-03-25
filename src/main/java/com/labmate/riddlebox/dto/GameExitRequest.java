package com.labmate.riddlebox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
// 게임 중단 정보를 담는 클래스
public class GameExitRequest {
    private Long gamePK;
    private int playTime;
    private int correctAnswers;
    private int incorrectAnswers;
    private String gameResult;

    public GameExitRequest(Long gamePK, int playTime, int correctAnswers, int incorrectAnswers, String gameResult) {
        this.gamePK = gamePK;
        this.playTime = playTime;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
        this.gameResult = gameResult;
    }

    public GameExitRequest(Long gamePK, int playTime, String gameResult) {
        this.gamePK = gamePK;
        this.playTime = playTime;
        this.gameResult = gameResult;
    }

}