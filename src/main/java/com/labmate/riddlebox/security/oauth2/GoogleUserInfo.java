package com.labmate.riddlebox.security.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleUserInfo {
    private String id;
    private String email;
    @JsonProperty("verified_email")
    private boolean verifiedEmail;
    private String name;
    @JsonProperty("given_name")
    private String givenName;
    @JsonProperty("family_name")
    private String familyName;
    private String picture;
    private String locale;

    // 기본 생성자
    public GoogleUserInfo() {
    }

    // 모든 필드를 포함한 생성자 (필요에 따라 사용)
    public GoogleUserInfo(String id, String email, boolean verifiedEmail, String name, String givenName, String familyName, String picture, String locale) {
        this.id = id;
        this.email = email;
        this.verifiedEmail = verifiedEmail;
        this.name = name;
        this.givenName = givenName;
        this.familyName = familyName;
        this.picture = picture;
        this.locale = locale;
    }
}
