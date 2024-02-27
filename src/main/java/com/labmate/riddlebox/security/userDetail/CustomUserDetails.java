package com.labmate.riddlebox.security;

import com.labmate.riddlebox.entity.RBRole;
import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.enumpackage.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.lang.annotation.Documented;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails, OAuth2User {

    private RBUser user; // 사용자

    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;  //OAuth2User 위한 것

    public CustomUserDetails(RBUser user,  Map<String, Object> attributes){
        this.user = user;
        this.attributes = attributes;
    }

    // 역할을 Spring Security의 GrantedAuthority로 변환
    private Collection<? extends GrantedAuthority> translateRolesToAuthorities(Set<RBRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return translateRolesToAuthorities(this.user.getRoles());
    }

    /**
     * login email 반환
     */
    @Override
    public String getUsername() {
        return this.user.getLoginEmail();
    }

    /**
     * PK반환
     */
    public Long getUserId() {
        return this.user.getId();
    }

    /**
     * 닉네임 반환
     */
    public String getUserNickname() {
        return this.user.getNickname();
    }

    /**
     * 실명 반환
     */
    public String getRealName() {
        return this.user.getName();
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.user.getStatus() != UserStatus.EXPIRED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.getStatus() != UserStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.getStatus() == UserStatus.ACTIVE;
    }


    @Override
    public String getName() {
        return null;
    }
}
