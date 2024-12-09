package com.sangjin.product.infrastructure;

import com.sangjin.product.dto.ItemDto;
import com.sangjin.product.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "api")
public interface ApiClient extends ApiService {

    @GetMapping("api/search/naver")
    public List<ItemDto> searchItems(@RequestParam String query);

}
