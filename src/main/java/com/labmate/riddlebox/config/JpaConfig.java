package com.labmate.riddlebox.config;

import com.labmate.riddlebox.security.userDetail.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

    //@CreatedBy와 @LastModifiedBy에 사용하기 위한 설정
    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            //SecurityContextHolder에서 인증 객체 꺼내오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            //getPrincipal : Authentication 객체에서 현재 인증된 사용자의 주요 정보를 나타냄
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                return Optional.of(userDetails.getUserId()); // 사용자 PK 반환
            }
            return Optional.empty();
        };
    }

}
