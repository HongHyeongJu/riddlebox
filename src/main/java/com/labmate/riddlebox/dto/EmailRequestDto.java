package com.labmate.riddlebox.dto;

import lombok.Data;

@Data
public class EmailRequestDto {

    private String toEmail;
    private String code;

}
