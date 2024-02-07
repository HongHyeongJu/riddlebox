package com.labmate.riddlebox.security;

import com.labmate.riddlebox.enumpackage.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private Long userId;
    private String userLoginId;
    private String userNickname;
    private UserRole userRole;
    private String password;

    public CustomUserDetails(Long userId, String userLoginId, String userNickname, UserRole userRole, String password) {
        this.userId = userId;
        this.userLoginId = userLoginId;
        this.userNickname = userNickname;
        this.userRole = userRole;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {  //권한 정보 반환
        return Collections.singletonList(new SimpleGrantedAuthority(userRole.name()));
    }

    @Override
    public String getUsername() {
        return null;
    }


    // TODO: 2024-01-16 기타 UserDetails 인터페이스 메서드 구현 (isAccountNonExpired, isAccountNonLocked(), isCredentialsNonExpired(), isEnabled 등)
    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부에 대한 로직
        return true; // 예시로, 항상 true 반환
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금 여부에 대한 로직
        return true; // 예시로, 항상 true 반환
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 자격 증명 만료 여부에 대한 로직
        return true; // 예시로, 항상 true 반환
    }

    @Override
    public boolean isEnabled() {
        // 계정 활성화 여부에 대한 로직
        return true; // 예시로, 항상 true 반환
    }


}
