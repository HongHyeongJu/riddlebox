package com.labmate.riddlebox.security;

import com.labmate.riddlebox.entity.RBRole;
import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.entity.SocialProfile;
import com.labmate.riddlebox.entity.UserRole;
import com.labmate.riddlebox.enumpackage.RoleStatus;
import com.labmate.riddlebox.enumpackage.UserRoleEnum;
import com.labmate.riddlebox.enumpackage.UserStatus;
import com.labmate.riddlebox.repository.RoleRepository;
import com.labmate.riddlebox.repository.SocialProfileRepository;
import com.labmate.riddlebox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private SocialProfileRepository socialProfileRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId(); // 예: "google"
        String providerId = oAuth2User.getAttribute("sub"); // 예: "103058387739722400130"
        String email = oAuth2User.getAttribute("email");
        String profilePictureURL = oAuth2User.getAttribute("picture");
        String name = oAuth2User.getAttribute("name"); // 또는 다른 이름 조합 로직
        String given_name = oAuth2User.getAttribute("given_name"); // 또는 다른 이름 조합 로직

        RBUser user = userRepository.findByLoginEmail(email)
                .orElseGet(() -> {
                    RBUser newUser = RBUser.builder()
                            .loginEmail(email)
                            .name(name)
                            .nickname(given_name)
                            .regDate(LocalDateTime.now())
                            .status(UserStatus.ACTIVE)
                            .lastLoginDate(LocalDateTime.now())
                            .build();
                    return newUser;
                });
        userRepository.save(user);

        // 역할이 'PLAYER'인지 확인하고, 없으면 새로 생성하여 저장
        RBRole role = roleRepository.findByName("PLAYER")
                .orElseGet(() -> {
                    RBRole newRole = new RBRole("PLAYER", "플레이어", 100, RoleStatus.ENABLED);
                    return roleRepository.save(newRole);
                });

        // 사용자 역할 연결
        UserRole userRole = new UserRole(user, role, LocalDateTime.now(), "System", true, "Assigned by OAuth2 login");
        user.addUserRole(userRole);

        // SocialProfile 처리
        SocialProfile socialProfile = SocialProfile.builder()
                .profilePictureURL(profilePictureURL)
                .socialAccessToken(null)
                .provider(provider)
                .providerId(providerId)
                .user(user)
                .build();
        socialProfileRepository.save(socialProfile);

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

}
