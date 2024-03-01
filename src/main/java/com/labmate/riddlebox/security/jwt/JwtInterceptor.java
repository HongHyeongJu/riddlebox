package com.labmate.riddlebox.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 요청 헤더에서 JWT 추출
        String jwt = request.getHeader(AUTHORIZATION_HEADER);

        // JWT가 존재하면, request에 attribute로 추가 (필요에 따라 사용)
        if (jwt != null && !jwt.isEmpty()) {
            request.setAttribute(AUTHORIZATION_HEADER, jwt);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // request attribute에서 JWT를 가져옴
        String jwt = (String) request.getAttribute(AUTHORIZATION_HEADER);

        // JWT가 존재하면, 응답 헤더에 추가
        if (jwt != null) {
            response.setHeader(AUTHORIZATION_HEADER, jwt);
        }
    }
}
