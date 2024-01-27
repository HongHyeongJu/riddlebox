package com.labmate.riddlebox.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class AnswerResponse {
    private boolean isCorrect; // 방금 제출한 문제의 정답 여부

    public AnswerResponse(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }


}
