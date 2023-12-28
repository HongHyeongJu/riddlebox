package com.labmate.riddlebox.enumpackage;

public enum GameLevel {

    EASY(100),
    MEDIUM(200),
    HARD(300);

    private final int level;

    GameLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }


}
