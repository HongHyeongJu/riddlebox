package com.labmate.riddlebox.security.jwt;

import com.labmate.riddlebox.security.SecurityConstants;
import com.labmate.riddlebox.security.SimplifiedPrincipalDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JwtTokenValidatorFilter extends OncePerRequestFilter {


    @Value("${my.secret.key}")
    private String JWT_KEY;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = request.getHeader(SecurityConstants.JWT_HEADER);

        if (null != jwt) {
            try {
                SecretKey key = Keys.hmacShaKeyFor(JWT_KEY.getBytes(StandardCharsets.UTF_8));

                Claims claims = Jwts.parser()
                                    .setSigningKey(key)
                                    .parseClaimsJws(jwt)
                                    .getBody();
                String loginEmail = String.valueOf(claims.get("loginEmail"));
                Long id = claims.get("id", Long.class);
                String nickname = String.valueOf(claims.get("nickname"));
                String authorities = (String) claims.get("authorities");
                SimplifiedPrincipalDetails principalDetails  = new SimplifiedPrincipalDetails(id, nickname, loginEmail, authorities);

                Authentication auth = new UsernamePasswordAuthenticationToken(principalDetails, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid Token received!");
            }

        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/user");
    }

}