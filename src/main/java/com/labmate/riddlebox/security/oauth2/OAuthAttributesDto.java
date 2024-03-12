package com.labmate.riddlebox.security.oauth2;


import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.enumpackage.UserStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class OAuthAttributesDto {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String pictureURL;

    @Builder
    public OAuthAttributesDto(Map<String, Object> attributes,
                           String nameAttributeKey,
                           String name, String email, String pictureURL) {

        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.pictureURL = pictureURL;
    }

    public static OAuthAttributesDto of(String registrationId,
            String userNameAttributeName,
            Map<String, Object> attributes) {

        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributesDto ofGoogle(String userNameAttributeName,
            Map<String, Object> attributes) {

        return OAuthAttributesDto.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .pictureURL((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public RBUser toEntity() {
        return RBUser.builder()
                    .loginEmail(email)
                    .name(name)
                    .regDate(LocalDateTime.now())
                    .lastLoginDate(LocalDateTime.now())
                    .status(UserStatus.ACTIVE)
                    .build();
    }


}