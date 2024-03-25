package com.labmate.riddlebox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
// 게임 결과 정보를 담는 클래스
public class GameCompletionRequest {
    private int totalQuestions;
    private int correctAnswersCount;
    private int playTime;
    private boolean isFail;

    public GameCompletionRequest(int totalQuestions, int correctAnswersCount, int playTime, boolean isFail) {
        this.totalQuestions = totalQuestions;
        this.correctAnswersCount = correctAnswersCount;
        this.playTime = playTime;
        this.isFail = isFail;
    }

}