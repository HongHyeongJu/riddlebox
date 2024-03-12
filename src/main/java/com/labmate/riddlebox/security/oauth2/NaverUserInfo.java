package com.labmate.riddlebox.security.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserInfo {
    private String id;
    private String email;
    private String nickname;

    @JsonProperty("response")
    private void unpackNested(Map<String, Object> response) {
        this.id = (String) response.get("id");
        this.nickname = (String) response.get("nickname");
        this.email = (String) response.get("email");
    }

    // 기본 생성자
    public NaverUserInfo() {
    }

    // 모든 필드를 포함한 생성자 (필요에 따라 사용)
    public NaverUserInfo(String id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }
}
