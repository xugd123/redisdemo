package com.example.redisdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisdemoApplication {

    public static void main(String[] args) {
        System.out.println("c2客户端");
        SpringApplication.run(RedisdemoApplication.class, args);
    }

}
