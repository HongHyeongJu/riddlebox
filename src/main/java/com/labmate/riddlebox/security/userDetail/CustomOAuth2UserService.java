package com.labmate.riddlebox.security.userDetail;

import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.repository.RoleRepository;
import com.labmate.riddlebox.repository.SocialProfileRepository;
import com.labmate.riddlebox.repository.UserRepository;
import com.labmate.riddlebox.security.oauth2.OAuthAttributesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("------------loadUser---------");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

		//로그인 진행중인 서비스를 구분하는 ID -> 여러 개의 소셜 로그인할 때 사용하는 ID
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        //OAuth2 로그인 진행 시 키가 되는 필드값(Primary Key) -> 구글은 기본적으로 해당 값 지원("sub")
        //그러나, 네이버, 카카오 로그인 시 필요한 값
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

		//OAuth2UserSevice를 통해 가져온 OAuth2User의 attribute를 담은 클래스
        OAuthAttributesDto attributes = OAuthAttributesDto.of(registrationId,
                userNameAttributeName, oAuth2User.getAttributes());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private RBUser saveOrSelect(OAuthAttributesDto attributes) {
        // 이메일을 기반으로 사용자 정보 조회
        Optional<RBUser> existingUser = userRepository.findByLoginEmail(attributes.getEmail());

        // 만약 사용자가 이미 존재한다면, 그대로 반환 (또는 필요에 따라 업데이트 로직 추가)
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            // 사용자가 존재하지 않는 경우, 새로운 사용자 생성 후 저장
            RBUser newUser = attributes.toEntity();
            return userRepository.save(newUser);
        }
    }
}