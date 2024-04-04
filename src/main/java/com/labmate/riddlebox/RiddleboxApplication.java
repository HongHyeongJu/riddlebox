package com.labmate.riddlebox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class RiddleboxApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RiddleboxApplication.class);
        app.setAdditionalProfiles("dev");
        app.run(args);
    }

}
