package com.labmate.riddlebox.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class GameDataDto {

    private Long gameid;
    private String gameTitle;
    private String illustrationImgPath;
    private String gameText;
    private String gameAnswer;
    private String gameEndTime;
    private String solverNickname;

    public GameDataDto(Long gameid, String gameTitle, String illustrationImgPath, String gameText, String gameAnswer, LocalDateTime gameEndTime, String solverNickname) {
        this.gameid = gameid;
        this.gameTitle = gameTitle;
        this.illustrationImgPath = illustrationImgPath;
        this.gameText = gameText;
        this.gameAnswer = gameAnswer;
        this.gameEndTime = gameEndTime.format(DateTimeFormatter.ISO_DATE_TIME); // "yyyy-MM-dd'T'HH:mm:ss" 형식
        this.solverNickname = solverNickname;
    }
}
