package com.ohgiraffers.secondbackend;

import com.ohgiraffers.secondbackend.user.client.EmailFeignClient;
import com.ohgiraffers.secondbackend.userlike.client.BookFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;



@EnableFeignClients(basePackageClasses = {BookFeignClient.class, EmailFeignClient.class})
@SpringBootApplication
public class UserServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
