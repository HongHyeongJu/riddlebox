package com.labmate.riddlebox.enumpackage;

public enum UserRole {

    PLAYER(100),
    MANAGER(200),
    ADMIN(300);

    private final int authority;

    UserRole(int authority) {
        this.authority = authority;
    }

    public int getAuthority() {
        return authority;
    }


}
