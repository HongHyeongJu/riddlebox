package com.labmate.riddlebox.security.oauth2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labmate.riddlebox.dto.SocialProfileDto;
import com.labmate.riddlebox.entity.RBUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import com.labmate.riddlebox.repository.UserRepository;
import com.labmate.riddlebox.security.PrincipalDetails;
import com.labmate.riddlebox.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserService userService;
    private ObjectMapper objectMapper = new ObjectMapper();


    @Transactional
    public boolean customLoadOAuth2UserForKaKao(String userInfoJson) {
        try {
            KakaoUserInfo userInfo = objectMapper.readValue(userInfoJson, KakaoUserInfo.class);
            Map<String, Object> userAttributes = objectMapper.readValue(userInfoJson, new TypeReference<Map<String, Object>>() {
            });

            RBUser user = userService.findUserByEmail(userInfo.getId() + "@kakao.com");
//            System.out.println("userInfo:   " + userInfo.toString());
            if (user == null) {
                //없으면 저장. userRole. socialProfile
                SocialProfileDto socialProfileDto = new SocialProfileDto("KAKAO",
                        userInfo.getId() + ""
                        , null,
                        null);
                user = userService.createAndSaveRBUser(userInfo.getId() + "@kakao.com",
                        userInfo.getNickname(),
                        userInfo.getNickname(), null,
                        socialProfileDto);

                //그리고 조회
            } else {
                user.setLastLoginDate(LocalDateTime.now());
                user = userRepository.save(user);
            }

            PrincipalDetails principalDetails = new PrincipalDetails(user, userAttributes);

            // 인증 객체 생성 및 SecurityContextHolder에 저장
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);


            // 인증 및 가입 과정이 성공적으로 완료되었다고 가정
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 예외 발생 시 실패로 처리
        }


    }




    @Transactional
    public boolean customLoadOAuth2UserForGoogle(String userInfoJson) {
        try {
            GoogleUserInfo userInfo = objectMapper.readValue(userInfoJson, GoogleUserInfo.class);
            Map<String, Object> userAttributes = objectMapper.readValue(userInfoJson, new TypeReference<Map<String, Object>>() {
            });

            RBUser user = userService.findUserByEmail(userInfo.getEmail());
            if (user == null) {
                //없으면 저장. userRole. socialProfile
                SocialProfileDto socialProfileDto = new SocialProfileDto("GOOGLE",
                        userInfo.getId()
                        , userInfo.getPicture(),
                        null);
                user = userService.createAndSaveRBUser(userInfo.getEmail(),
                        userInfo.getFamilyName()+userInfo.getGivenName(),
                        userInfo.getName(), null,
                        socialProfileDto);

                //그리고 조회
            } else {
                user.setLastLoginDate(LocalDateTime.now());
                user = userRepository.save(user);
            }

            PrincipalDetails principalDetails = new PrincipalDetails(user, userAttributes);


            // 인증 객체 생성 및 SecurityContextHolder에 저장
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);


            // 인증 및 가입 과정이 성공적으로 완료되었다고 가정
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 예외 발생 시 실패로 처리
        }


    }




    @Transactional
    public boolean customLoadOAuth2UserForNaver(String userInfoJson) {

        try {
            NaverUserInfo userInfo = objectMapper.readValue(userInfoJson, NaverUserInfo.class);
            Map<String, Object> userAttributes = objectMapper.readValue(userInfoJson, new TypeReference<Map<String, Object>>() {
            });

            RBUser user = userService.findUserByEmail(userInfo.getEmail());
            if (user == null) {
                //없으면 저장. userRole. socialProfile
                SocialProfileDto socialProfileDto = new SocialProfileDto("NAVER",
                        userInfo.getId() + ""
                        , null,
                        null);
                user = userService.createAndSaveRBUser(userInfo.getEmail(),
                        userInfo.getNickname(),
                        userInfo.getNickname(), null,
                        socialProfileDto);

                //그리고 조회
            } else {
                user.setLastLoginDate(LocalDateTime.now());
                user = userRepository.save(user);
            }

            PrincipalDetails principalDetails = new PrincipalDetails(user, userAttributes);

            // 인증 객체 생성 및 SecurityContextHolder에 저장
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 인증 및 가입 과정이 성공적으로 완료되었다고 가정
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 예외 발생 시 실패로 처리
        }


    }


    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("---------CustomOAuth2UserService의 --- loadUser 자동호출 ---------");

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
                Collections.singleton(new SimpleGrantedAuthority("ROLE_PLAYER")),
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