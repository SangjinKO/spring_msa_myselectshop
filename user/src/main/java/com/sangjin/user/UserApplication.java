package com.sangjin.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserApplication {
    public static void main(String[] args) {
        System.out.println("USER APPLICATION STARTED");
        SpringApplication.run(UserApplication.class, args);
    }
}