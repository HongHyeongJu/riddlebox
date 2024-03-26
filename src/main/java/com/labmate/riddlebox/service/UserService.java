package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.SignupRequestDto;
import com.labmate.riddlebox.dto.SocialProfileDto;
import com.labmate.riddlebox.entity.RBUser;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    boolean isValidNickname(String nickname);

    boolean isEmailUnique (String email);


    RBUser findUserByEmail(String email);


    @Transactional
    RBUser createAndSaveRBUser(String loginEmail, String name, String nickname, String password, SocialProfileDto socialProfileDto);

    void signupNewUser(SignupRequestDto signupRequestDto);
}
