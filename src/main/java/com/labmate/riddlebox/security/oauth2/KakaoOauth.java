package com.labmate.riddlebox.security.oauth2;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class KakaoOauth {

    private final String KAKAO_LOGIN_URI = "https://kauth.kakao.com/oauth/authorize";
    private final String KAKAO_TOKEN_REQURST_URI = "https://kauth.kakao.com/oauth/token";
    private final String KAKAO_USERINFO_REQUEST_URI = "https://kapi.kakao.com/v2/user/me";
    private final String KAKAO_USER_NAME_ATTRIBUTE = "id";


    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecert;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoClientRedirectUri;

    public String getOauthRedirectUrl() {
        return KAKAO_LOGIN_URI + "?" +
                "response_type=code" +
                "&client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoClientRedirectUri;
    }
}
//https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}