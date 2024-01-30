package com.labmate.riddlebox.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Question {
    private Long gameContentId;
    private String question;

    public Question(Long gameContentId, String question) {
        this.gameContentId = gameContentId;
        this.question = question;
    }
}