package com.labmate.riddlebox.entity;

import jakarta.persistence.*;

@Entity
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
    private String socialAccessToken;

    public SocialProfile(RBUser user, String provider,
                         String providerId, String profilePictureURL,
                         String socialAccessToken) {
        this.user = user;
        this.provider = provider;
        this.providerId = providerId;
        this.profilePictureURL = profilePictureURL;
        this.socialAccessToken = socialAccessToken;
    }

    public void setUser(RBUser user) {
        this.user = user;
    }

}
