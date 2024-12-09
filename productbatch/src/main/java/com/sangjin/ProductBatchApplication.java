package com.sangjin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductBatchApplication {
    public static void main(String[] args) {

        System.out.println("PRODUCT BATCH APPLICATION STARTED");
        SpringApplication.run(ProductBatchApplication.class, args);
    }
}