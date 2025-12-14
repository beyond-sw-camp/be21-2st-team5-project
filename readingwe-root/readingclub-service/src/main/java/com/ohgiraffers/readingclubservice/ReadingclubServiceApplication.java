package com.ohgiraffers.readingclubservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class ReadingclubServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReadingclubServiceApplication.class, args);
    }

}
