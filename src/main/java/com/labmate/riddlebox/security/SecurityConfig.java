package com.labmate.riddlebox.security;

import com.labmate.riddlebox.security.loginhandler.CustomOAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;  // Spring Security 필터 체인을 정의
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// 보안 관련 처리를 위해 HTTP 요청을 가로채고 처리하는 일련의 필터들로 구성
@Configuration
@EnableWebSecurity // Spring Security 설정을 활성화
public class SecurityConfig {  //Spring Security의 보안 구성을 정의하는 클래스

    @Bean
    public AuthenticationSuccessHandler oauth2LoginSuccessHandler() {
        return new CustomOAuth2LoginSuccessHandler();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http    .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/game/**", "api/games/**", "/support/inquiry/**",
                                "/mypage/**","/mypage/record").hasRole("PLAYER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/payment/**").hasRole("PAY_PLAYER")
                        .requestMatchers("/logout").authenticated()
                        .requestMatchers("/", "/index", "/games", "/resources/**", "/css/**", "/js/**", "/img/**", "/formlogin", "/test/**",
                                "/supports/**", "/login", "/account/recovery", "/signup/**", "/oauth2/**", "/auth/**","/favicon.ico","/error/**","/health").permitAll())
                .csrf((csrf) -> csrf.ignoringRequestMatchers("/","/index", "/games", "/login", "/supports/**",
                                            "/signup/send-email", "/signup/validate-code", "/oauth2/**", "/auth/**",
                                            "/formlogin", "/error/**","/test/**","/api/games/**","/health"))
                .addFilterBefore(new SecurityContextPersistenceFilter(), BasicAuthenticationFilter.class)

                .headers((headers) -> headers.addHeaderWriter(
                        new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))

                .formLogin((formLogin) -> formLogin.loginPage("/login")
                        .defaultSuccessUrl("/index"))

                .logout((logout) -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/index")
                        .invalidateHttpSession(true))
        .exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedPage("/error/custom-403-page")); // Here



        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
