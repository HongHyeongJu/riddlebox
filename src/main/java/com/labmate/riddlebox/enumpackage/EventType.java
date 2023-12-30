package com.labmate.riddlebox.enumpackage;

public enum EventType {
    LOGIN("사용자 로그인"),
    LOGOUT("사용자 로그아웃"),
    GAME_START("게임 시작"),
    GAME_PAUSE("일시 정지"),
    GAME_RESUME("게임 재개"),
    GAME_EXIT("게임 이탈"),
    GAME_END("게임 종료"),
    LEVEL_UP("레벨 업"),
    ACHIEVEMENT("업적 달성"),
    SCORE_UPDATE("점수 업데이트"),
    PAUSE("게임 일시 정지"),
    RESUME("게임 재개");

    private final String description;

    EventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

