package com.labmate.riddlebox.security.userDetail;

import com.labmate.riddlebox.entity.RBRole;
import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RBUser user = userRepository.findByLoginEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(user.getLoginEmail(), user.getPassword(),getGrantedAuthorities(user.getRoles()));
    }


    private List<GrantedAuthority> getGrantedAuthorities(Set<RBRole> roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (RBRole role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }
        return grantedAuthorities;
    }


}
