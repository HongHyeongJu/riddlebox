package com.labmate.riddlebox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TimeLimitGameDto {
    private String gameTitle;
    private String illustrationImgPath;
    private String gameText;
    private String gameEndTime;
    private List<CommentDto> commentDtoList;
    private String solverNickname;

    public TimeLimitGameDto(String gameTitle, String illustrationImgPath, String gameText,
                            LocalDateTime gameEndTime, List<CommentDto> commentDtoList, String solverNickname) {
        this.gameTitle = gameTitle;
        this.illustrationImgPath = illustrationImgPath;
        this.gameText = gameText;
        this.gameEndTime = String.valueOf(gameEndTime);
        this.commentDtoList = commentDtoList;
        this.solverNickname = solverNickname;
    }

}
