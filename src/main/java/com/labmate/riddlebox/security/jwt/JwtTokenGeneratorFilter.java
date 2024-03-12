package com.labmate.riddlebox.security.jwt;

import com.labmate.riddlebox.security.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

// JWT 토큰 생성 필터, Spring Security의 OncePerRequestFilter를 상속받아 구현
public class JwtTokenGeneratorFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String JWT_KEY;

    // HTTP 요청을 처리하는 메소드. 필터 체인 내에서 한 번만 실행됨
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 현재 인증 정보를 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
            // 설정된 시크릿 키를 기반으로 JWT 서명 키를 생성
            SecretKey key = Keys.hmacShaKeyFor(JWT_KEY.getBytes(StandardCharsets.UTF_8));

            // Jwts 빌더를 사용하여 JWT 토큰을 생성하기
            String jwt = Jwts.builder().setIssuer("Riddle-Box").setSubject("JWT Token")
                    .claim("username", authentication.getName()) // 사용자 이름을 페이로드에 추가
                    .claim("authorities", populateAuthorities(authentication.getAuthorities())) //// 사용자 권한을 페이로드에 추가
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + 30000000))
                    .signWith(key).compact(); // 서명 알고리즘과 키를 사용하여 토큰을 서명하고 압축

            // 생성된 JWT 토큰을 HTTP 응답 헤더에 추가
            response.setHeader(SecurityConstants.JWT_HEADER, jwt);
        }

        // 나머지 필터 체인을 계속 실행
        filterChain.doFilter(request, response);
    }


    // 특정 요청 경로에 대해서는 이 필터를 적용하지 않도록 설정 -> 이거 왜만들었는지 모르겠다
    //보안 필터를 적용할 필요가 없거나, 특정 처리를 우회해야 하는 요청
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return pathMatcher.match("/css/**", request.getServletPath()) ||
               pathMatcher.match("/js/**", request.getServletPath()) ||
               pathMatcher.match("/img/**", request.getServletPath()) ||
               pathMatcher.match("/supports/**", request.getServletPath())||
               pathMatcher.match("/login/**", request.getServletPath())||
               pathMatcher.match("/signup/**", request.getServletPath()) ||
               pathMatcher.match("/index", request.getServletPath()) ||
               pathMatcher.match("/", request.getServletPath());
    }


    // Authentication 객체에서 권한 정보를 추출하여 문자열로 변환하는 메소드
    private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> authoritiesSet = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            authoritiesSet.add(authority.getAuthority());
        }
        return String.join(",", authoritiesSet);
    }

}