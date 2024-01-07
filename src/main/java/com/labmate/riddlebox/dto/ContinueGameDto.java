package com.labmate.riddlebox.dto;

import lombok.Data;

@Data
public class ContinueGameDto {

    private Long gameId;
    private String gameTitle;
    private String lastPlayedDate;

}
