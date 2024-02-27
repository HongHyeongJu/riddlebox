package com.labmate.riddlebox.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private RBUser user;

    private String provider;
    private String providerId;
    private String profilePictureURL;
    private String refreshToken;

    @Builder
    public SocialProfile(RBUser user, String provider,
                         String providerId, String profilePictureURL,
                         String refreshToken) {
        this.user = user;
        this.provider = provider;
        this.providerId = providerId;
        this.profilePictureURL = profilePictureURL;
        this.refreshToken = refreshToken;
    }

    public void setUser(RBUser user) {
        if (this.user != null) {
            this.user.getSocialProfiles().remove(this);
        }
        this.user = user;
    }

}
