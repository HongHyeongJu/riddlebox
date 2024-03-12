package com.labmate.riddlebox.security;

import com.labmate.riddlebox.entity.RBRole;
import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.entity.UserRole;
import com.labmate.riddlebox.enumpackage.UserStatus;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ToString
public class PrincipalDetails implements UserDetails, OAuth2User {

    private RBUser user;
    private Map<String, Object> attributes; //구글 로그인을 통해서 받은 정보들을 그대로 담아 return 해주는 역할

    public PrincipalDetails(RBUser user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return user.getLoginEmail();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자의 권한 정보를 담을 리스트를 생성합니다.
        List<GrantedAuthority> authorities = new ArrayList<>();

        // RBUser의 권한 목록을 순회하며, 각 권한에 대한 SimpleGrantedAuthority 객체를 생성하여 리스트에 추가합니다.
        for (UserRole userRole : user.getUserRoles()){
            if (Boolean.TRUE.equals(userRole.getIsActive())) { // 활성화된 권한만을 고려합니다.
                RBRole role = userRole.getRole(); // UserRole에서 Role 엔터티를 가져옵니다.
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }
        }

        // 생성된 권한 목록을 반환합니다.
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }
    public String getNickname() {
        return user.getNickname();
    }
    public Long getUserPK() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getStatus() != UserStatus.EXPIRED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != UserStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.getStatus() != UserStatus.EXPIRED;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }
}
