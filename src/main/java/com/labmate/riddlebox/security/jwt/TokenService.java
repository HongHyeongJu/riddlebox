package com.labmate.riddlebox.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateToken(String username, String email, Collection<? extends GrantedAuthority> authorities) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)); // 변수명 jwtSecret로 수정
        String jwt = Jwts.builder()
                .setIssuer("Issuer Name")
                .setSubject("JWT Token")
                .claim("username", username)
                .claim("email", email) // 이메일 정보 추가
                .claim("authorities", authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 30000000)) // Expiry time 설정
                .signWith(key)
                .compact();

        return jwt;
    }
}
