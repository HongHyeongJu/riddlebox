package com.labmate.riddlebox.enumpackage;

public enum FaqCategory {
    HOMEPAGE("홈페이지"),
    USAGE_ACCOUNT("이용 및 계정 문의"),
    TUTORIAL("튜토리얼"),
    CONTENT_SUGGESTION("컨텐츠제안"),
    TECHNICAL_ISSUE("기술문제/버그"),
    GAME_UPDATE("게임업데이트"),
    EVENT_PROMOTION("이벤트/프로모션");

    private final String description;

    FaqCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
