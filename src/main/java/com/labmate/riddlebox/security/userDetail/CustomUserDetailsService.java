/*
package com.labmate.riddlebox.security;

import com.labmate.riddlebox.entity.Admin;
import com.labmate.riddlebox.entity.Member;
import com.labmate.riddlebox.repository.AdminRepository;
import com.labmate.riddlebox.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        // MemberRepository에서 사용자를 찾기
        Optional<Member> memberOptional = memberRepository.findByLoginId(loginId);

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            return new CustomUserDetails(member.getId(), member.getLoginId(), member.getNickname(), member.getRole(), member.getPassword());
        } else {
            // AdminRepository에서 사용자를 찾기
            Optional<Admin> adminOptional = adminRepository.findByLoginId(loginId);
            Admin admin = adminOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with login ID: " + loginId));

            return new CustomUserDetails(admin.getId(), admin.getLoginId(), admin.getNickname(), admin.getRole(), admin.getPassword());
        }
    }

//
//    * 실제로 로그인한 사용자의 정보를 얻으려면
//    * 보안 컨텍스트(Security Context)에서
//    * Authentication 객체를 가져와서
//    * CustomUserDetails로 캐스팅한 다음, 필요한 정보를 얻기
//    *



}
*/
