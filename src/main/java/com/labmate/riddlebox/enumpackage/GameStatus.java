package com.labmate.riddlebox.enumpackage;

public enum GameStatus {

    ACTIVE("Active"),       // 사용 중인 게임
    DRAFT("Draft"),         // 만들어두고 아직 게시하지 않은 게임
    HIDDEN("Hidden"),       // 잠시 사용 못하게 숨겨진 게임
    DELETED("Deleted"),     // 삭제된 게임
    UPDATING("Updating");   // 수정 중인 게임

    private final String status;

    GameStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}