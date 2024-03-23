package com.labmate.riddlebox.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // 이 설정 클래스는 특별한 빈을 정의하지 않아도 됨
    // @EnableAsync 애너테이션 자체로 스프링의 비동기 처리 기능을 활성화함
}
