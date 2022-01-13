package com.example.studywithme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StudyWithMeApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyWithMeApplication.class, args);
    }
}
