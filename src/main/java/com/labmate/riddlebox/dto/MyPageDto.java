package com.labmate.riddlebox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserAnswerDto {
    private Long gameContentId;
    private String userAnswer;

    public UserAnswerDto(Long gameContentId, String userAnswer) {
        this.gameContentId = gameContentId;
        this.userAnswer = userAnswer;
    }


}
