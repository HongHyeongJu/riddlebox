package com.labmate.riddlebox.enumpackage;


public enum NoticeCategory {
    UPDATE_PATCH("업데이트 및 패치"),
    EVENT("이벤트"),
    MAINTENANCE("유지보수"),
    POLICY_CHANGE("규정 변경"),
    IMPORTANT("중요 공지"),
    SERVICE_CHANGE("서비스 변경"),
    SECURITY_UPDATE("보안 업데이트"),
    USER_GUIDE("사용자 가이드"),
    COMMUNITY_NEWS("커뮤니티 소식");

    private final String description;

    NoticeCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

