package com.labmate.riddlebox.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequestDto {

    @NotBlank
    @Size(max = 50)
    @Email
    private String loginEmail;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    public LoginRequestDto(String loginEmail, String password) {
        this.loginEmail = loginEmail;
        this.password = password;
    }
}
