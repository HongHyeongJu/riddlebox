package com.labmate.riddlebox.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnswerKeyDto {

    private Long gameContentId;
    private String correctAnswer;
}
