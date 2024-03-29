package com.labmate.riddlebox.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    //dir: E:/riddleboxfile/
    //dir: resources/static/img/
    @Value("${file.s3.directory}")
    private String fileDir;

    // 웹 접근을 위한 기본 URL
    @Value("${file.accessUrl}")
    private String fileAccessUrl;


    public String getFullPath(String filename, String fileTypeDir) {
        if("image".equals(fileTypeDir)){
            return fileDir + "image/" + filename;
        } else if ("text".equals(fileTypeDir)) {
            return fileDir + "text/" + filename;
        } else if ("video".equals(fileTypeDir)) {
            return fileDir + "video/" + filename;
        }
        return fileDir + filename;
    }


    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {

        List<UploadFile> storeFileResult = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originFileName = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originFileName);
        //현재는 image만 저장할 꺼라서 바로 명시해둠
        multipartFile.transferTo(new File(getFullPath(storeFileName,"image")));

        // 파일의 실제 경로와 웹 URL 추가
        String filePath = getFullPath(storeFileName,"image");
        String fileUrl = fileAccessUrl + storeFileName;

        // 파일 크기
        Long fileSize = multipartFile.getSize();

        return new UploadFile(originFileName, storeFileName,
                              filePath, fileSize, fileUrl);
    }

    private String createStoreFileName(String originalFilename) {

        String ext = getExtension(originalFilename); //확장자
        String uuid = UUID.randomUUID().toString(); //UUID

        return uuid + "." + ext;  // UUID.확장자
    }

    private String getExtension(String originalFilename) {  //확장자 뽑기 메서드

        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
