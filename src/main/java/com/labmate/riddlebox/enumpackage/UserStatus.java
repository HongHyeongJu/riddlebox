package com.labmate.riddlebox.enumpackage;

public enum UserStatus {

    ACTIVE("Active"), //사용가능 계정
    DORMANT("Dormant"), //휴면계정
    SUSPENDED("Suspended"), //정지상태
    DELETED("Deleted");  //삭제상태

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

