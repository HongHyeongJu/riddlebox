package com.labmate.riddlebox.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication; // Authentication!!!
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {


    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        // 여기서 사용자 ID를 가져오는 방식은 애플리케이션의 인증 구조에 따라 다를 수 있음
        // 예를 들어, UserDetails를 사용하는 경우 아래와 같이 사용자 ID를 추출할 수 있음
        // String userId = ((UserDetails) authentication.getPrincipal()).getUsername();
        // return Optional.ofNullable(userId);

        // 세션에서 사용자 ID를 직접 가져오는 경우
        // 예를 들어, 세션에 사용자 ID가 'userId'라는 이름으로 저장된 경우
        String userId = (String) authentication.getDetails();
        return Optional.ofNullable(userId);
    }
}
