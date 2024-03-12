package com.labmate.riddlebox.util;

import lombok.Data;

@Data
public class UploadFile {

    private String originFileName;
    private String storeFileName;
    private String fileType;  //일러스트, 썸네일, 임시이미지(Enum으로 변경은 나중에...)
    private String filePath;  //파일경로
    private Long fileSize;  //파일크기
    private String fileUrl;  //file_url

    public UploadFile(String originFileName, String storeFileName,
                      String filePath, Long fileSize, String fileUrl) {
        this.originFileName = originFileName;
        this.storeFileName = storeFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
    }


    public void changeImageType(String newFileType){
        this.fileType = newFileType;
    }


}
