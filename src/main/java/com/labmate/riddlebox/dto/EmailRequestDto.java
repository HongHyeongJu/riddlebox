package com.labmate.riddlebox.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EmailRequestDto {

    private String toEmail;
    private String code;

    public EmailRequestDto(String toEmail, String code) {
        this.toEmail = toEmail;
        this.code = code;
    }
}
