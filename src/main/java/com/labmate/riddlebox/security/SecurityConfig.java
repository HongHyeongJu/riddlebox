package com.labmate.riddlebox.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;  // Spring Security 필터 체인을 정의
                                        // 보안 관련 처리를 위해 HTTP 요청을 가로채고 처리하는 일련의 필터들로 구성
@Configuration
@EnableWebSecurity // Spring Security 설정을 활성화
public class SecurityConfig  {  //Spring Security의 보안 구성을 정의하는 클래스

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//                            //AuthenticationManagerBuilder는 Spring Security에서 사용자 인증을 구성하는 데 사용되는 핵심 객체
//        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
//                                                                        //BCryptPasswordEncoder의 인스턴스를 사용
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/login", "/register").permitAll() // 로그인과 회원가입 URL은 누구나 접근 가능하도록 설정
                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
            .and()
            .formLogin()
                .loginPage("/login") // 로그인 페이지 URL 설정
                .defaultSuccessUrl("/") // 로그인 성공 시 리디렉션될 URL
                .failureUrl("/login?error=true") // 로그인 실패 시 리디렉션될 URL
                .usernameParameter("loginId") // 로그인 폼에서 사용자 ID로 사용되는 파라미터명
                .passwordParameter("password") // 로그인 폼에서 비밀번호로 사용되는 파라미터명
            .and()
            .logout()
                .logoutSuccessUrl("/login"); // 로그아웃 성공 시 리디렉션될 URL
    }



    @Bean                                   //HttpSecurity 객체를 사용하여 보안 관련 세부 설정
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                            //filterChain 메서드는 HttpSecurity 객체를 매개변수로 받아 보안 구성을 정의
        http
                .authorizeRequests(authorizeRequests ->
//                authorizeRequests.anyRequest().authenticated()  //모든 요청에 대해 인증을 필요로 하도록 설정
                                authorizeRequests.anyRequest().permitAll()  // 모든 요청에 대해 보안 비활성화
                ).formLogin().disable().httpBasic().disable(); // HTTP 기본 인증 비활성화

//            .formLogin()  //폼 기반 로그인을 활성화  이거 유데미 강의 듣고 수정
//            .and()
//            .httpBasic();  //HTTP 기본 인증을 활성화

        return http.build();
    }
}
