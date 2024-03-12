package com.labmate.riddlebox.security.oauth2;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class NaverOauth {

    private final String NAVER_LOGIN_URI = "https://nid.naver.com/oauth2.0/authorize";
    private final String NAVER_TOKEN_REQURST_URI = "https://nid.naver.com/oauth2.0/token";
    private final String NAVER_USERINFO_REQUEST_URI = "https://openapi.naver.com/v1/nid/me";
    private final String NAVER_USER_NAME_ATTRIBUTE = "response";


    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecert;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverClientRedirectUri;

    public String getOauthRedirectUrl() {
        return NAVER_LOGIN_URI +
                "?response_type=code" +
                "&client_id=" + naverClientId +
                "&redirect_uri=" + naverClientRedirectUri +
                "&state=test";
    }
}
