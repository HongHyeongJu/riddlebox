package com.labmate.riddlebox.enumpackage;

public enum FaqCategory {
    USAGE("이용문의"),
    ACCOUNT("계정문의"),
    GAME_CONTENT("게임컨텐츠문의"),
    GENERAL("일반문의"); // 또는 GENERAL("일반문의")

    private final String description;

    FaqCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
