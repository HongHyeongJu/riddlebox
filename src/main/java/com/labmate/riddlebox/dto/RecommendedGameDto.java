package com.labmate.riddlebox.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecommendedGameDto {

    private Long id;
    private String title;
    private String thumbnailUrl; // 게임 썸네일

}
