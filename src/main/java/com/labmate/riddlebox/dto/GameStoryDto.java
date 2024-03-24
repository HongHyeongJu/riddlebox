package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.entity.GameCategory;
import com.labmate.riddlebox.enumpackage.GameLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@Getter
@NoArgsConstructor
public class GameStoryDto {

    private Long id;  //게임 번호
    private String illustUrl;  //이미지 url
    private String storyText;  //소설 내용



}
