package com.labmate.riddlebox.api;

import com.labmate.riddlebox.dto.EmailRequestDto;
import com.labmate.riddlebox.service.EmailService;
import com.labmate.riddlebox.service.RedisService;
import com.labmate.riddlebox.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/signup")
public class ApiUserController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserService userService;

    private static final String AUTH_CODE_PREFIX = "AuthCode_";

    private final Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    private JavaMailSender mailSender;


    /* 인증 메일 전송 */
    @PostMapping("/send-email")
    public CompletableFuture<ResponseEntity<String>> sendEmail(@RequestBody EmailRequestDto request) {

                                                                      //thenApply() : 비동기 연산의 결과를 받아 다른 형태로 변환할 때 사용
                                                                      //              비동기 연산의 결과를 연쇄적으로 변환하고 처리 가능
                                                                      //CompletableFuture 객체가 완료되었을 때 실행될 함수를 인자로 받음
        return emailService.makeMailContentAndSaveCodeToRedis(request.getToEmail()).thenApply(map -> {
            if (map.isEmpty()) {
                logger.error("이메일 전송 실패. 받는 사람: {}", request.getToEmail());
                return ResponseEntity.badRequest().body("이미 사용 중인 이메일 주소입니다.");
            }

            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(username);
                helper.setTo(request.getToEmail());
                helper.setSubject(map.get("title"));
                helper.setText(map.get("sendText"), true); // HTML 형식으로 설정

                mailSender.send(message);

                logger.info("이메일로 인증번호가 성공적으로 전송되었습니다. 받는 사람: {}", request.getToEmail());
                return ResponseEntity.ok("이메일로 인증번호가 전송되었습니다.");
            } catch (MessagingException e) {
                logger.error("이메일 전송 중 오류 발생: {}", e.getMessage());
                return ResponseEntity.internalServerError().body("이메일 전송 중 오류가 발생했습니다.");
            }
        });
    }


    /* 메일 인증번호 검증 */
    @PostMapping("/validate-code")
    public boolean validateCode(@RequestBody EmailRequestDto request) {
        return redisService.validateCode(AUTH_CODE_PREFIX + request.getToEmail(), request.getCode());
    }


    @PostMapping("/validate-nickname")
    public ResponseEntity<Boolean> validateNickname(@RequestParam("nickname") String nickname) {
        boolean isValid = userService.isValidNickname(nickname);
        return ResponseEntity.ok(isValid);
    }


}
