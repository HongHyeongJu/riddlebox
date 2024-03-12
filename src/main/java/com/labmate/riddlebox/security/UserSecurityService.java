package com.labmate.riddlebox.security;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.labmate.riddlebox.dto.SocialProfileDto;
import com.labmate.riddlebox.entity.RBRole;
import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.repository.UserRepository;
import com.labmate.riddlebox.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RBUser findUser = userService.findUserByEmail(username);
        if (findUser==null) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }

        PrincipalDetails principalDetails = new PrincipalDetails(findUser, null);

        // 인증 객체 생성 및 SecurityContextHolder에 저장
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return principalDetails;
    }


//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<RBUser> findUser = this.userRepository.findByLoginEmail(username);
//        if (findUser.isEmpty()) {
//            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
//        }
//        RBUser user = findUser.get();
//        List<GrantedAuthority> authorities = getGrantedAuthorities(user.getRoles());
//        return new User(user.getLoginEmail(), user.getPassword(), authorities);
//    }

    private List<GrantedAuthority> getGrantedAuthorities(Set<RBRole> roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (RBRole role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }
        return grantedAuthorities;
    }

}