package com.labmate.riddlebox.security.loginhandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        System.out.println("----------실패 원인 로깅-----------");

        // OAuth2AuthenticationException으로부터 OAuth2Error 추출
        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oAuth2Exception = (OAuth2AuthenticationException) exception;
            OAuth2Error error = oAuth2Exception.getError();

            // OAuth2 인증 실패에 대한 추가적인 오류 정보 로깅
            log.error("OAuth2 Error Code: {}", error.getErrorCode());
            log.error("OAuth2 Error Description: {}", error.getDescription());

            // 클라이언트에 보낼 응답 설정
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"" + error.getErrorCode() + "\", \"error_description\":\"" + error.getDescription() + "\"}");
        } else {
            // 기존 로깅 및 처리 로직
            log.error("OAuth2 Authentication Failed: {}", exception.getMessage(), exception);
            setDefaultFailureUrl("/login?error=true");
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}