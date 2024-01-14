package com.labmate.riddlebox.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public class AnswerResponse {
    private boolean isCorrect; // 방금 제출한 문제의 정답 여부

    public AnswerResponse(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
