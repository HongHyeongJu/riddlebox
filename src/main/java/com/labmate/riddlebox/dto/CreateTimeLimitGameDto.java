package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.ImageType;
import com.labmate.riddlebox.util.TypeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateTimeLimitGameDto {

    private String gameTitle;  //제목
    private String gameDescription;  //제목
    private String gameText;  //게임 텍스트
    private String question;  //문제(아마 안보여줄꺼임)
    private String answer;  //정답
    private String explanation;  //해설
    private String gameLevel;
    private String author;
    private String illustrationImgPath;
    private LocalDate gameEndTime;

    public CreateTimeLimitGameDto(String gameTitle, String gameDescription, String gameText, String question, String answer, String explanation,
                                  String gameLevel, String author, String illustrationImgPath, LocalDate gameEndTime) {
        this.gameTitle = gameTitle;
        this.gameDescription = gameDescription;
        this.gameText = gameText;
        this.question = question;
        this.explanation = explanation;
        this.answer = answer;
        this.gameLevel = gameLevel;
        this.author = author;
        this.illustrationImgPath = illustrationImgPath;
        this.gameEndTime = gameEndTime;
    }


    public GameLevel getGameLevelType() {
        return TypeConverter.convertGameLevelFromString(gameLevel);
    }

}
