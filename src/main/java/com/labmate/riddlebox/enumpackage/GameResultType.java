package com.labmate.riddlebox.enumpackage;

public enum GameResultType {
    SOLVED("해결", "게임을 성공적으로 완료함"),
    UNSOLVED("미해결", "기회 내에 게임을 해결하지 못함"),
    ABANDONED("중도포기", "게임을 중간에 포기함"),
    TIME_EXPIRED("시간초과", "제한 시간 내에 게임을 완료하지 못함"),
    DISCONNECTED("연결끊김", "네트워크 문제 등으로 게임이 중단됨"),
    ABNORMAL_TERMINATION("비정상종료", "게임이 예상치 못한 방식으로 종료됨");

    private final String title;
    private final String description;

    GameResultType(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
