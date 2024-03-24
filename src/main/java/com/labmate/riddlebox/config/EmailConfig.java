package com.labmate.riddlebox.config;

import com.labmate.riddlebox.api.ApiUserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class EmailConfig {

    private static final String AUTH_CODE_PREFIX = "AuthCode_";

    private final Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host); // SMTP 서버 주소
        mailSender.setPort(587); // SMTP 서버 포트

        mailSender.setUsername(username); // 이메일 계정
        mailSender.setPassword(password); // 이메일 비밀번호

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false"); // 필요한 경우 디버깅을 위해 활성화

        return mailSender;
    }
}
