package com.labmate.riddlebox.security;

import com.labmate.riddlebox.security.userDetail.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication; // Authentication!!!
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

// JPA의 AuditorAware 인터페이스를 구현한 것, 이 클래스의 목적은 현재 로그인한 사용자의 정보를 JPA auditing과 연결하는 것
//데이터의 변경 이력을 관리하기 위한 JPA auditing과 관련된 클래스
//`SpringSecurityAuditorAware` 클래스는 `SecurityContextHolder`를 사용하여 현재 로그인한 사용자의 이름(또는 ID)을 `@CreatedBy` 필드에 자동으로 설정하기 위한 정보를 제공합니다.
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

    @Override               //getCurrentAuditor 메서드는 현재 인증된 사용자의 정보를 제공(이 정보는 JPA 엔티티의 생성자나 수정자 필드에 자동으로 할당됨)
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        // 여기서 사용자 ID를 가져오는 방식은 애플리케이션의 인증 구조에 따라 다를 수 있음
        // 예를 들어, UserDetails를 사용하는 경우 아래와 같이 사용자 ID를 추출할 수 있음
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return Optional.ofNullable(userDetails.getUserId());

    }

}


//JPA auditing은 엔티티가 생성되거나 수정될 때, 해당 작업을 수행한 사용자의 정보를 자동으로 엔티티에 저장하는 기능을 제공
