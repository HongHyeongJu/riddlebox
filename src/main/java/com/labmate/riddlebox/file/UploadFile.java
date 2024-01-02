package com.labmate.riddlebox.file;

import lombok.Data;

@Data
public class UploadFile {

    private String originFileName;
    private String storeFileName;
    private String fileType;
    private String filePath;  //파일경로
    private Long fileSize;  //파일크기
    private String fileUrl;  //file_url

    public UploadFile(String originFileName, String storeFileName,
                      String fileType, String filePath,
                      Long fileSize, String fileUrl) {
        this.originFileName = originFileName;
        this.storeFileName = storeFileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
    }
}
