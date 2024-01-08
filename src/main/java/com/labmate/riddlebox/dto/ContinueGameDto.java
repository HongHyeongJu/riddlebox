package com.labmate.riddlebox.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContinueGameDto {

    private Long gameId;
    private String gameTitle;
    private String lastPlayedDate;

}
