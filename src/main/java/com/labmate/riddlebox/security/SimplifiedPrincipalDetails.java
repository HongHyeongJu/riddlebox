package com.labmate.riddlebox.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class SimplifiedPrincipalDetails implements UserDetails, OAuth2User {

    private Long id;
    private String nickname;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;

    public SimplifiedPrincipalDetails(Long id, String nickname, String email, String roles) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(roles));
    }

    // UserDetails and OAuth2User methods implementation
    @Override
    public String getName() {
        return String.valueOf(id); // OAuth2User method
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of("id", id, "nickname", nickname, "email", email);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // JWT 방식에서는 일반적으로 패스워드를 다루지 않음
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
