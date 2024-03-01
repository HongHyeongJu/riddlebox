package com.labmate.riddlebox.service;

public interface UserService {

    boolean isValidNickname(String nickname);

    boolean checkDuplicateEmail(String email);


}
