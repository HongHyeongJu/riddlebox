package com.labmate.riddlebox.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
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


    @Override
    //사용자 이메일로 코드 전송
    public Map<String, String> makeMailContentAndSaveCodeToRedis(String toEmail) {
        if (!userService.checkDuplicateEmail(toEmail)) {
            return new ConcurrentHashMap<>();
        }

        Map map = new ConcurrentHashMap<>();

        String title = "RiddleBox 회원가입 이메일 인증번호";
        String authCode = createCode();
        String sendText = "<h2>RiddleBox 회원가입 이메일 인증번호</h2>" +
                "<p>귀하의 회원가입 이메일 인증번호는 <h3>" + authCode + "</h3> 입니다. " +
                "인증번호는 30분 이후 만료됩니다.</p>" +
                "<p>사이트 방문하기: <a href='http://RiddleBox.com'>RiddleBox.com</a></p>" +
                "<p>문의사항이 있으시면, <a href='mailto:riddlebox2024@gmail.com'>riddlebox2024@gmail.com</a>로 연락주세요.</p>";
        String sendTextTest = "RiddleBox 회원가입 인증번호 ["+authCode+"] (30분 뒤 만료)";

        map.put("title", title);
        map.put("sendText", sendText);
        map.put("sendTextTest", sendTextTest);

        System.out.println("====="+toEmail+", "+title+", "+authCode);

        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisService.setValues(AUTH_CODE_PREFIX + toEmail , authCode, Duration.ofMinutes(30)); //만료시간 30분!

        return map;
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


    //코드 인증하기
    //redisService.validateCode(AUTH_CODE_PREFIX + email, authCode)

//    @Override
//    public void sendEmail(String toEmail,
//                          String title,
//                          String text) {
//
//        SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);
//
//        try {
//            javaMailSender.send(emailForm);
//        } catch (RuntimeException e) {
////            log.debug("MailService.sendEmail exception occur toEmail: {}, " +
////                    "title: {}, text: {}", toEmail, title, text);
////            throw new BusinessLogicException(ExceptionCode.UNABLE_TO_SEND_EMAIL);
//        }
//    }
//
//    @Override
//    // 발신할 이메일 데이터 세팅
//    public SimpleMailMessage createEmailForm(String toEmail,
//                                             String title,
//                                             String text) {
//
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//
//        simpleMailMessage.setTo(toEmail);
//        simpleMailMessage.setSubject(title);
//        simpleMailMessage.setText(text);
//
//        return simpleMailMessage;
//    }


}
