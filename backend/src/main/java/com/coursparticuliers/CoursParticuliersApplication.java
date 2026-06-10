package com.coursparticuliers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoursParticuliersApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoursParticuliersApplication.class, args);
    }
}
