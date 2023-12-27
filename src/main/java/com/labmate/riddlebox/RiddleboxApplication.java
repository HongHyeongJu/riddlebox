package com.labmate.riddlebox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RiddleboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiddleboxApplication.class, args);
    }

//    @Bean
//     public AuditorAware<String> auditorProvider() {
//     return () -> Optional.of(UUID.randomUUID().toString());
//     }

}
