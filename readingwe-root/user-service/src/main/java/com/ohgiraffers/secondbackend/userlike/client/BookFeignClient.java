package com.ohgiraffers.secondbackend.userlike.client;

import com.ohgiraffers.secondbackend.userlike.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "book-service", configuration = FeignClientConfig.class)
public interface BookFeignClient {

    @GetMapping("/books/categories")
    List<String> getBookCategories();
}
