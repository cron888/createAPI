package org.example.createapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CreateApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreateApiApplication.class, args);
    }
}
