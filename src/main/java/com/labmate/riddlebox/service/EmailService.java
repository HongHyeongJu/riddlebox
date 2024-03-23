package com.labmate.riddlebox.service;

import org.springframework.mail.SimpleMailMessage;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface EmailService {
    // 발신할 이메일 데이터 세팅

//    SimpleMailMessage createEmailForm(String toEmail,
//                                      String title,
//                                      String text);

//    void sendEmail(String toEmail,
//                   String title,
//                   String text);

    //사용자 이메일로 코드 전송
//    boolean sendCodeToEmail(String toEmail);

    //사용자 이메일로 코드 전송
    CompletableFuture<Map<String, String>> makeMailContentAndSaveCodeToRedis(String toEmail);

    boolean sendMailOnePass(String toEmail,
                            String title,
                            String text);

//    void mail_test();
}
