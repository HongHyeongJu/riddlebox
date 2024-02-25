package com.labmate.riddlebox.enumpackage;

public enum UserStatus {

    ACTIVE("Active - 사용 가능한 계정"),
    LOCKED("Locked - 보안 문제 등의 이유로 잠긴 계정"),
    EXPIRED("Expired - 사용 기간이 만료된 계정"),
    DISABLED("Disabled - 비활성화된 계정");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

