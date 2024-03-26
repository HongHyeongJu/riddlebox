package com.labmate.riddlebox.security;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labmate.riddlebox.dto.SocialProfileDto;
import com.labmate.riddlebox.entity.RBRole;
import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.repository.UserRepository;
import com.labmate.riddlebox.security.oauth2.KakaoUserInfo;
import com.labmate.riddlebox.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class UsernamePwdAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        RBUser user = userService.findUserByEmail(authentication.getName());

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + authentication.getName());
        }

        // 비밀번호 검증
        if(!passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        // 사용자 마지막 로그인 시간 업데이트
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);

        // 사용자의 권한 정보를 포함한 PrincipalDetails 객체 생성
        PrincipalDetails principalDetails = new PrincipalDetails(user, null);

        // 권한 목록 가져오기
        Collection<? extends GrantedAuthority> authorities = principalDetails.getAuthorities();


        // 인증 객체 생성 및 반환
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principalDetails, null, authorities);
        // SecurityContextHolder에 새로운 인증 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        return authenticationToken;


    }


    private List<GrantedAuthority> getGrantedAuthorities(Set<RBRole> roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (RBRole role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }
        return grantedAuthorities;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}


//   @Transactional
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//
//        System.out.println("                  ==============================");
//        System.out.println("                UsernamePwdAuthenticationProvider");
//        System.out.println("                  ==============================");
//
//        String username = authentication.getName();  //이메일 반환
//        String pwd = authentication.getCredentials().toString();
//        Optional<RBUser> userOptional = userRepository.findByLoginEmail(username);
//
//        return userOptional.map(user
//                -> {if (passwordEncoder.matches(pwd, user.getPassword())) {
//                        return new UsernamePasswordAuthenticationToken(username, pwd, getGrantedAuthorities(user.getRoles()));
//                    } else {
//                        throw new BadCredentialsException("Invalid password!");
//                    }}).orElseThrow(() -> new BadCredentialsException("No user registered with this details!"));
//
//    }
