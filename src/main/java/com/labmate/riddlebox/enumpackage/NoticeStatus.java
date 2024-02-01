package com.labmate.riddlebox.enumpackage;

public enum NoticeStatus {
    POSTED("게시"), // 공지사항 및 FAQ에서 공통적으로 사용될 수 있는 상태
    URGENT("긴급공지"),
    SCHEDULED("공지예정"),
    DELETED("삭제");

    private final String description;

    NoticeStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
