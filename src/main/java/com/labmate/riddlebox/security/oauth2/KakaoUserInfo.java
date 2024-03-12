package com.labmate.riddlebox.security.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfo {
    private Long id;
    private String nickname;
    private String email;

    @JsonProperty("properties")
    private void unpackProperties(Map<String, Object> properties) {
        this.nickname = (String) properties.get("nickname");
    }

    @JsonProperty("kakao_account")
    private void unpackKakaoAccount(Map<String, Object> kakaoAccount) {
        this.email = (String) kakaoAccount.get("email");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile != null) {
            this.nickname = (String) profile.get("nickname"); // 프로필에서 닉네임을 우선적으로 사용합니다.
        }
    }

    // 기본 생성자
    public KakaoUserInfo() {
    }

    // 모든 필드를 포함한 생성자 (필요에 따라 사용)
    public KakaoUserInfo(Long id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }
}
