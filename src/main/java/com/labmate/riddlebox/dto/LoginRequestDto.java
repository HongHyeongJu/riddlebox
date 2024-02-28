package com.labmate.riddlebox.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequestDto {

    @NotBlank
    private String loginEmail;

    @NotBlank
    private String password;

    public LoginRequestDto(String loginEmail, String password) {
        this.loginEmail = loginEmail;
        this.password = password;
    }
}
