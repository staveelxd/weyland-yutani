package com.weyland.bishop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BishopPrototypeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BishopPrototypeApplication.class, args);
    }
}