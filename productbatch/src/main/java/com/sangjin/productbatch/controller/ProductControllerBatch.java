package com.sangjin.productbatch.controller;

import com.sangjin.productbatch.service.UpdateProductScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/batch")
public class ProductControllerBatch {

    private final UpdateProductScheduler updateProductScheduler;

        // updatePriceSchedulerTest (상품가격 갱신 스케줄러) 테스트하기
    @GetMapping("/products/update-price-scheduler-test")
    public ResponseEntity<String> updatePriceSchedulerTest(){
        try {
            updateProductScheduler.updatePrice();
        } catch (InterruptedException e) {
            ResponseEntity.status(HttpStatus.OK)
                    .body(e.toString());
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("scheduler finished");
    }

}
