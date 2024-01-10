package com.labmate.riddlebox.enumpackage;

public enum UserRole {

    BLACKLIST(50),
    PLAYER(100),
    PAY_PLAYER(200),
    MANAGER(300),
    ADMIN(500);

    private final int authority;

    UserRole(int authority) {
        this.authority = authority;
    }

    public int getAuthority() {
        return authority;
    }


}
