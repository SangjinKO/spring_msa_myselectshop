package com.sangjin.product.controller;

import com.sangjin.product.dto.ItemDto;
import com.sangjin.product.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final ApiService apiService;

    // NAVER api (FeignClient) 를 사용하여 상품 조회하기
    @GetMapping("/product-search")
    public List<ItemDto> searchItems(@RequestParam String query)  {

        return apiService.searchItems(query);
    }

}

