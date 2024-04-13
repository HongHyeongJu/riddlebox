package com.labmate.riddlebox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TimeLimitGameDto {
    private Long gameid;
    private String gameTitle;
    private String illustrationImgPath;
    private String gameText;
    private String gameAnswer;
    private String gameEndTime;
    private List<CommentDto> commentDtoList;
    private String solverNickname;
    private int currentPage;
    private int totalPages;


    public TimeLimitGameDto(Long gameid, String gameTitle, String illustrationImgPath, String gameText,String gameAnswer,
                            LocalDateTime gameEndTime, List<CommentDto> commentDtoList, String solverNickname) {
        this.gameid = gameid;
        this.gameTitle = gameTitle;
        this.illustrationImgPath = illustrationImgPath;
        this.gameText = gameText;
        this.gameAnswer = gameAnswer;
         // LocalDateTime을 ISO 8601 문자열 형식으로 변환
        this.gameEndTime = gameEndTime.format(DateTimeFormatter.ISO_DATE_TIME); // "yyyy-MM-dd'T'HH:mm:ss" 형식
        this.commentDtoList = commentDtoList;
        this.solverNickname = solverNickname;
    }





    public TimeLimitGameDto(Long gameid, String gameTitle, String illustrationImgPath, String gameText,String gameAnswer,
                            LocalDateTime gameEndTime, List<CommentDto> commentDtoList, String solverNickname,
                            int currentPage, int totalPages) {
        this.gameid = gameid;
        this.gameTitle = gameTitle;
        this.illustrationImgPath = illustrationImgPath;
        this.gameText = gameText;
        this.gameAnswer = gameAnswer;
         // LocalDateTime을 ISO 8601 문자열 형식으로 변환
        this.gameEndTime = gameEndTime.format(DateTimeFormatter.ISO_DATE_TIME); // "yyyy-MM-dd'T'HH:mm:ss" 형식
        this.commentDtoList = commentDtoList;
        this.solverNickname = solverNickname;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

}
