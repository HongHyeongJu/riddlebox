package com.labmate.riddlebox.service;

import com.labmate.riddlebox.entity.Member;
import com.labmate.riddlebox.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {






























// --------------------------메일 인증 관련---------------------------


    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    private final MemberRepository memberRepository;

    private final MailService mailService;

    private final RedisService redisService;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;


    //사용중인 메일인지 확인하기
    private void checkDuplicateEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }
    }

    //사용자 이메일로 코드 전송
    public void sendCodeToEmail(String toEmail) {
        checkDuplicateEmail(toEmail);
        String title = "Travel with me 이메일 인증 번호";
        String authCode = createCode();
        mailService.sendEmail(toEmail, title, authCode);
        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisService.setValues(AUTH_CODE_PREFIX + toEmail,
                authCode, Duration.ofMillis(this.authCodeExpirationMillis));
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

    //코드 인증하기
    public EmailVerificationResult verifyCode(String email, String authCode) {
        checkDuplicateEmail(email);
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);
        boolean authResult = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);

        return EmailVerificationResult.of(authResult);
    }


}
