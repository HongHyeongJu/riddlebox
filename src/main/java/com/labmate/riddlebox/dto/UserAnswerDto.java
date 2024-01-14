package com.labmate.riddlebox.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAnswerDto {

    private Long gameId;
    private Long gameContentId;
    private String answer;
    private boolean isCorrect;

}
