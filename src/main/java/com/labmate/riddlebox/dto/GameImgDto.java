package com.labmate.riddlebox.dto;


import com.labmate.riddlebox.enumpackage.ImageType;
import lombok.Data;

@Data
public class GameImgDto {

    private Long imgId;
    private ImageType fileType;  //파일타입
    private String filePath;  //파일경로

}
