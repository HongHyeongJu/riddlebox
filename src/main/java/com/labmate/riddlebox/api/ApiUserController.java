package com.labmate.riddlebox.api;

import com.labmate.riddlebox.dto.EmailRequestDto;
import com.labmate.riddlebox.service.EmailService;
import com.labmate.riddlebox.service.RedisService;
import com.labmate.riddlebox.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping
public class ApiUserController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserService userService;

    private static final String AUTH_CODE_PREFIX = "AuthCode_";

    private final Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port:587}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;


    /* 인증 메일 전송 */
    @PostMapping("/signup/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDto request) {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        SimpleMailMessage message = new SimpleMailMessage();
        Map map = emailService.makeMailContentAndSaveCodeToRedis(request.getToEmail());

        message.setFrom(username);
        message.setTo(request.getToEmail());
        message.setSubject((String) map.get("title"));
        message.setText((String)map.get("sendText"));

        mailSender.send(message);

        redisService.setValues(AUTH_CODE_PREFIX + request.getToEmail(),
                "111111", Duration.ofMinutes(30)); //만료시간 30분!

//        boolean emailSent = emailService.sendCodeToEmail(request.getToEmail());

        return ResponseEntity.ok("이메일로 인증번호가 전송되었습니다.");

/*        if (emailSent) {
            logger.info("이메일로 인증번호가 성공적으로 전송되었습니다. 받는 사람: {}", request.getToEmail());
            return ResponseEntity.ok("이메일로 인증번호가 전송되었습니다.");
        } else {
            logger.error("이메일 전송 실패. 받는 사람: {}", request.getToEmail());
            return ResponseEntity.badRequest().body("이메일 전송 실패.");
        }*/
    }


    /* 메일 인증번호 검증 */
    @PostMapping("/signup/validate-code")
    public boolean validateCode(@RequestBody EmailRequestDto request) {
        return redisService.validateCode(AUTH_CODE_PREFIX + request.getToEmail(), request.getCode());
    }


    @PostMapping("/signup/validate-nickname")
    public ResponseEntity<Boolean> validateNickname(@RequestParam("nickname") String nickname) {
        boolean isValid = userService.isValidNickname(nickname);
        return ResponseEntity.ok(isValid);
    }


}
