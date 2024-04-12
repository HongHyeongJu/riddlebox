package com.labmate.riddlebox.enumpackage;

public enum GameSubject {
    SNAPSHOT_DEDUCTION("snapshot"), // 사진을 보고 추리하는 게임
    SHORT_STORY("story"), // 짧은 이야기를 다루는 게임
    EMOJI_GAME("emoji"), // 이모지를 사용하는 게임
    TIME_LIMIT("time_limit"), // 시간제한 게임
    MYSTERY ("미스터리"), // 공포나 미스터리 요소가 강한 이야기
    ADVENTURE("모험"), // 모험과 탐험을 중심으로 한 이야기
    EVERYDAY("일상"); // 일상적인 배경과 사건을 다루는 이야기

    private final String description;

    GameSubject(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
