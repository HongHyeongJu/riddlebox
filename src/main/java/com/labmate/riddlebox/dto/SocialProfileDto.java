package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.entity.RBUser;
import jakarta.persistence.*;
import lombok.*;


@Getter
public class SocialProfileDto {

    private Long id;
    private String provider;
    private String providerId;
    private String profilePictureURL;
    private String refreshToken;

    @Builder
    public SocialProfileDto( String provider,
                         String providerId, String profilePictureURL,
                         String refreshToken) {
        this.provider = provider;
        this.providerId = providerId;
        this.profilePictureURL = profilePictureURL;
        this.refreshToken = refreshToken;
    }
}