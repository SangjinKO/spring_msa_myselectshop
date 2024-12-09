package com.sangjin.naverapi.controller;

import com.sangjin.naverapi.dto.ItemDto;
import com.sangjin.naverapi.service.NaverApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NaverApiController {

    private final NaverApiService naverApiService;

    @GetMapping("/search/naver")
    public List<ItemDto> searchItems(@RequestParam String query)  {

        return naverApiService.searchItems(query);
    }
}
