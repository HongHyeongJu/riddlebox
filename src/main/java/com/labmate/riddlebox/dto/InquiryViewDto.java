package com.labmate.riddlebox.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class InquirySubmitDto {

    @NotBlank
    private String faqCategory;  //FaqCategory

    @NotBlank
    private String question;

    @NotBlank
    private String content;

    @NotBlank
    private LocalDateTime inquiryAt;

    public InquirySubmitDto(String faqCategory, String question, String content, LocalDateTime inquiryAt) {
        this.faqCategory = faqCategory;
        this.question = question;
        this.content = content;
        this.inquiryAt = inquiryAt;
    }
}
