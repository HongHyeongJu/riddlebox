package com.labmate.riddlebox.enumpackage;

public enum GameSubject {
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
