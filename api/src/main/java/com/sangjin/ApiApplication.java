package com.sangjin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {
    public static void main(String[] args) {
        System.out.println("API APPLICATION STARTED");
        SpringApplication.run(ApiApplication.class, args);
    }
}