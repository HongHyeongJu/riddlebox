package com.labmate.riddlebox.enumpackage;


public enum ImageType {
    THUMBNAIL("썸네일"),
    ILLUSTRATION("일러스트"),
    TEMPORARY("임시이미지"),
    LOGO("로고"),
    SCREENSHOT("스크린샷"); // 게임 스크린샷

    private final String description;

    ImageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

