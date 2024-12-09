package com.sangjin.product.controller;

import com.sangjin.product.config.security.CustomUserPrincipal;
import com.sangjin.product.dto.ProductMypriceRequestDto;
import com.sangjin.product.dto.ProductRequestDto;
import com.sangjin.product.dto.ProductResponseDto;
import com.sangjin.product.service.ProductService;
import com.sangjin.product.service.UpdateProductKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;
    private final UpdateProductKafkaProducer updateProductKafkaProducer;

    // 관심 상품 등록하기
    @PostMapping("/products")
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto requestDto, @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        // 응답 보내기
        return productService.createProduct(requestDto, userPrincipal.getUserId());
    }

    // 관심 상품 희망 최저가 등록하기
    @PutMapping("/products/{id}")
    public ProductResponseDto updateProduct(@PathVariable Long id, @RequestBody ProductMypriceRequestDto requestDto) {
        // 응답 보내기
        return productService.updateProduct(id, requestDto);
    }

    // 관심 상품 조회하기
    @GetMapping("/products")
    public Page<ProductResponseDto> getProducts(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        // 응답 보내기
        return productService.getProducts(userPrincipal,  page-1, size, sortBy, isAsc);
    }

    // 관심 상품 조회하기
    @GetMapping("/productsAll")
    public List<ProductResponseDto> getProductsAll(@AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        // 응답 보내기
        return productService.getProductsAll(userPrincipal);
    }

    // 관리자 조회
    @GetMapping("/admin/products")
    public List<ProductResponseDto> getAllProducts() {
        return productService.getAllProducts();
    }

    // 상품에 폴더 추가
    @PostMapping("/products/{productId}/folder")
    public void addFolder(
            @PathVariable Long productId,
            @RequestParam Long folderId,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal
    ) {
        productService.addFolder(productId, folderId, userPrincipal);
    }

    // 회원이 등록한 폴더 내 모든 상품 조회
    @GetMapping("/folders/{folderId}/products")
    public Page<ProductResponseDto> getProductsInFolder(
            @PathVariable Long folderId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam boolean isAsc,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal
    ) {
        return productService.getProductsInFolder(
                folderId,
                page-1,
                size,
                sortBy,
                isAsc,
                userPrincipal.getUserId()
        );
    }

    @GetMapping("/folders/{folderId}/productsAll")
    public List<ProductResponseDto> getProductsInFolderAll(
            @PathVariable Long folderId,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal
    ) {
        return productService.getProductsInFolderAll(
                folderId,
                userPrincipal.getUserId()
        );
    }

//    // updatePriceSchedulerTest (상품가격 갱신 스케줄러) 테스트하기
//    @GetMapping("/products/update-price-scheduler-test")
//    public ResponseEntity<String> updatePriceSchedulerTest(){
//        try {
//            updateProductScheduler.updatePrice();
//        } catch (InterruptedException e) {
//            ResponseEntity.status(HttpStatus.OK)
//                    .body(e.toString());
//        }
//        return ResponseEntity.status(HttpStatus.OK)
//                .body("scheduler finished");
//    }
//
    // updatePriceKafka (상품가격 갱신: 카프카 Producer)
    @GetMapping("/products/update-price-kafka")
    public ResponseEntity<String> updatePriceKafka(){
        try {
            updateProductKafkaProducer.updatePriceKafka();
        } catch (InterruptedException e) {
            ResponseEntity.status(HttpStatus.OK)
                    .body(e.toString());
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("kafka producer finished");
    }

}
