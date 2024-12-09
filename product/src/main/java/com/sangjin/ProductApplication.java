package com.sangjin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ProductApplication {
    public static void main(String[] args) {

        System.out.println("PRODUCT APPLICATION STARTED");
        SpringApplication.run(ProductApplication.class, args);
    }
}