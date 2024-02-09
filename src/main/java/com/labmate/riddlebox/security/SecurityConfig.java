package com.labmate.riddlebox.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;  // Spring Security 필터 체인을 정의
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// 보안 관련 처리를 위해 HTTP 요청을 가로채고 처리하는 일련의 필터들로 구성
@Configuration
@EnableWebSecurity // Spring Security 설정을 활성화
public class SecurityConfig  {  //Spring Security의 보안 구성을 정의하는 클래스

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/예외처리하고 싶은 url", "/예외처리하고 싶은 url");
    }


     @Bean
    protected SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests()
                .requestMatchers("/로그인페이지", "/css/**", "/images/**", "/js/**").permitAll()
                .anyRequest().authenticated()

            .and()
                .formLogin()
                .loginPage("/로그인페이지")
                .loginProcessingUrl("/실제 로그인이 되는 url")
                .permitAll()
                .successHandler(로그인 성공 시 실행할 커스터마이즈드 핸들러)
                .failureHandler(로그인 실패 시 실행할 커스터마이즈드 핸들러);

        http
                .sessionManagement()
                .invalidSessionUrl("/로그인페이지")

            .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/실제 로그아웃이 되는 url"))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll();


        //CSRF 토큰
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

        return http.build();
    }




//    새로운 SecurityFilterChain 방식으로 전환하면서 필요 없어진 코드들은 주로 @Override로 정의된 configure 메서드들
//    @Override
//    protected void configure(HttpSecurity http)
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception


}
