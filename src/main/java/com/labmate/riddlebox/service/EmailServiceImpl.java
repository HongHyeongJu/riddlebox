package com.labmate.riddlebox.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Transactional
public class EmailServiceImpl implements EmailService {

    private static final String AUTH_CODE_PREFIX = "AuthCode_";

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port:587}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;


    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;


    //사용자 이메일로 코드 전송
    @Override
    @Async  // 이 메서드를 비동기적으로 실행하기 위해 @Async 애너테이션 추가하기
    public CompletableFuture<Map<String, String>> makeMailContentAndSaveCodeToRedis(String toEmail) {
        Map<String, String> map = new ConcurrentHashMap<>();

        if (!userService.isEmailUnique(toEmail)) {
            return CompletableFuture.completedFuture(map);
        }


        String title = "RiddleBox 회원가입 이메일 인증번호";
        String authCode = createCode();
        String sendText = "<h2>RiddleBox 회원가입 이메일 인증번호</h2>" +
                "<p>귀하의 회원가입 이메일 인증번호는 <h3>" + authCode + "</h3> 입니다. " +
                "인증번호는 30분 이후 만료됩니다.</p>" +
                "<p>사이트 방문하기 : <a href='https://riddle-box.com'> riddle-box.com </a></p>" +
                "<p>문의사항이 있으시면, <a href='mailto:riddlebox2024@gmail.com'>riddlebox2024@gmail.com</a>로 연락주세요.</p>";
        String sendTextTest = "RiddleBox 회원가입 인증번호 ["+authCode+"] (30분 뒤 만료)";

        map.put("title", title);
        map.put("sendText", sendText);
        map.put("sendTextTest", sendTextTest);

        System.out.println("====="+toEmail+", "+title+", "+authCode);

        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisService.setValues(AUTH_CODE_PREFIX + toEmail , authCode, Duration.ofMinutes(30)); //만료시간 30분!

        // CompletableFuture를 사용해서 비동기적으로 결과 반환하기!
        return CompletableFuture.completedFuture(map);
                                //completedFuture 메서드는
                                // 인자로 주어진 값을 가지고 즉시 완료 상태인 CompletableFuture 객체를 생성함
    }


    //6자리 코드 만들기
    private String createCode() {
        int length = 6;
        Random random = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }


    @Override
    public boolean sendMailOnePass(String toEmail, String title, String text) {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password); // 환경 변수나 외부 설정 파일에서 가져온 비밀번호 사용

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(username);
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        try {
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("이메일 전송 실패. 받는 사람: {}, 오류 메시지: {}", toEmail, e.getMessage());
            return false;
        }

    }


}