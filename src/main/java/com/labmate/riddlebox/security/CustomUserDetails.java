package com.labmate.riddlebox.security;

import com.labmate.riddlebox.entity.RBRole;
import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.enumpackage.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.annotation.Documented;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private Long userId; // 사용자 엔티티의 PK
    private String loginEmail;
    private String password;
    private String name;
    private String nickname;
    private UserStatus status;
    private LocalDate passwordSetDate; // 비밀번호 설정일
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(RBUser user) {
        this.userId = user.getId(); // User 엔티티로부터 사용자 ID를 설정
        this.loginEmail = user.getLoginEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.passwordSetDate = user.getPasswordSetDate(); // 비밀번호 설정일 설정
        this.status = user.getStatus();
        this.authorities = translateRolesToAuthorities(user.getRoles());
    }

    // 역할을 Spring Security의 GrantedAuthority로 변환
    private Collection<? extends GrantedAuthority> translateRolesToAuthorities(Set<RBRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * login email 반환
     */
    @Override
    public String getUsername() {
        return loginEmail;
    }

    /**
     * PK반환
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 닉네임 반환
     */
    public String getUserNickname() {
        return nickname;
    }

    /**
     * 실명 반환
     */
    public String getRealName() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status != UserStatus.EXPIRED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }


}
