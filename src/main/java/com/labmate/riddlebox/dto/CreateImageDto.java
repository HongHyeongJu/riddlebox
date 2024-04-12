package com.labmate.riddlebox.dto;


import com.labmate.riddlebox.enumpackage.ImageType;
import com.labmate.riddlebox.util.TypeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateImageDto {

    private String fileOriginName;
    private String fileSaveName;
    private String imageTypeString;
    private String filePath;
    private Long fileSize;
    private String fileUrl;
    private String description;

    public CreateImageDto(String fileOriginName, String fileSaveName, String imageTypeString, String filePath, Long fileSize, String fileUrl, String description) {
        this.fileOriginName = fileOriginName;
        this.fileSaveName = fileSaveName;
        this.imageTypeString = imageTypeString;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.description = description;
    }

    public ImageType getImageType() {
        return TypeConverter.convertImageTypeFromString(imageTypeString);
    }

}
