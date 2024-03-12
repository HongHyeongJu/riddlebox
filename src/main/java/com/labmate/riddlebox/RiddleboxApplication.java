package com.labmate.riddlebox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RiddleboxApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RiddleboxApplication.class);
        app.setAdditionalProfiles("oauth");
        app.run(args);
    }

}
