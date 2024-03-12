package com.labmate.riddlebox.security.oauth2;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class GoogleOauth {

    private final String GOOGLE_LOGIN_URI = "https://accounts.google.com/o/oauth2/auth";
    private final String GOOGLE_TOKEN_REQURST_URI = "https://oauth2.googleapis.com/token";
    private final String GOOGLE_USERINFO_REQUEST_URI = "https://www.googleapis.com/oauth2/v3/userinfo";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecert;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleClientRedirectUri;

    public String getOauthRedirectUrl() {
        return GOOGLE_LOGIN_URI +"?"+
                "client_id=" + googleClientId +
                "&redirect_uri=" + googleClientRedirectUri +
                "&response_type=code" +
                "&scope=openid email profile" + // 스코프 추가
                "&access_type=offline"; // 필요한 경우, 리프레시 토큰을 요청하기 위해 추가
    }
}
