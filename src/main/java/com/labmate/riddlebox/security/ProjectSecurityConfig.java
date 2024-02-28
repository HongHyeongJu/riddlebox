package com.labmate.riddlebox.security;

import com.labmate.riddlebox.repository.RoleRepository;
import com.labmate.riddlebox.repository.SocialProfileRepository;
import com.labmate.riddlebox.repository.UserRepository;
import com.labmate.riddlebox.security.jwt.JwtTokenGeneratorFilter;
import com.labmate.riddlebox.security.jwt.JwtTokenValidatorFilter;
import com.labmate.riddlebox.security.loginhandler.CustomAuthenticationFailureHandler;
import com.labmate.riddlebox.security.loginhandler.CustomFormLoginSuccessHandler;
import com.labmate.riddlebox.security.loginhandler.CustomOAuth2LoginSuccessHandler;
import com.labmate.riddlebox.security.userDetail.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;  // Spring Security 필터 체인을 정의
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

// 보안 관련 처리를 위해 HTTP 요청을 가로채고 처리하는 일련의 필터들로 구성
@Configuration
@EnableWebSecurity // Spring Security 설정을 활성화
public class ProjectSecurityConfig {  //Spring Security의 보안 구성을 정의하는 클래스


    // 핸들러 빈으로 등록
    @Bean
    public CustomFormLoginSuccessHandler customFormLoginSuccessHandler() {
        return new CustomFormLoginSuccessHandler();
    }
    @Bean
    public CustomOAuth2LoginSuccessHandler customOAuth2LoginSuccessHandler() {
        return new CustomOAuth2LoginSuccessHandler();
    }
    @Bean
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SocialProfileRepository socialProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 서비스 빈으로 등록
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
        return new CustomOAuth2UserService(userRepository, roleRepository, socialProfileRepository, passwordEncoder);
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return new com.labmate.riddlebox.security.CustomUserDetailsService();
    }



    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.setAllowCredentials(true);
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setExposedHeaders(Arrays.asList("Authorization"));
                config.setMaxAge(3600L);
                return config;
            }
        })).csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers("/contact","/register")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JwtTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JwtTokenValidatorFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests((requests)->requests
                        .requestMatchers("/game/**","api/games/**","/support/inquiry/**",
                                            "/mypage/**").hasRole("PLAYER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/payment/**").hasRole("PAY_PLAYER")
                        .requestMatchers("/logout").authenticated()
                        .requestMatchers("/", "/index", "/games", "/resources/**", "/css/**", "/js/**",
                        "/supports/**", "/login").permitAll())
                .formLogin(form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/perform_login")
                    .defaultSuccessUrl("/index", true)
                    .failureUrl("/login?error=true")
                    .successHandler(customFormLoginSuccessHandler())
                    .failureHandler(customAuthenticationFailureHandler())
                    .permitAll())
                .oauth2Login(oauth2 -> oauth2
                    .loginPage("/login")
                    .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService()))
                    .successHandler(customOAuth2LoginSuccessHandler())
                    .failureHandler(customAuthenticationFailureHandler()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
