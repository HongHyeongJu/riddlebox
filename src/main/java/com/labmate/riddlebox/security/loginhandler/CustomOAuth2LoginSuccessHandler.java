package com.labmate.riddlebox.security.loginhandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class CustomOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 여기에 로그인 성공 후 수행할 로직을 구현합니다.
        // 예를 들어, 사용자를 데이터베이스에 저장하거나 로그를 남기는 작업 등
        response.sendRedirect("/index"); // 기본적으로 홈 페이지로 리다이렉트
    }



}
