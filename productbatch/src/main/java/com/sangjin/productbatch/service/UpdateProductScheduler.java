package com.sangjin.productbatch.service;


import com.sangjin.product.dto.ItemDto;
import com.sangjin.product.repository.ProductRepository;
import com.sangjin.product.repository.Product;
import com.sangjin.product.service.ApiService;
import com.sangjin.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "Scheduler")
@Component
@RequiredArgsConstructor
public class UpdateProductScheduler {

    @Qualifier("com.sangjin.product.infrastructure.ApiClient")
    private final ApiService apiService;
    private final ProductService productService;
    private final ProductRepository productRepository;

    // 초, 분, 시, 일, 월, 주 순서
//    @Scheduled(cron = "*/10 * * * * *") // test: 10초 마다
//    @Scheduled(cron = "0 0 9 * * *") // 매일 아침 9시
    public void updatePrice() throws InterruptedException {
        log.info("*** 가격 업데이트 스케줄러 실행 ***");
        List<Product> productList = productRepository.findAll();
        int selectCnt = productList.size();
        log.info("-----저장된 모든 상품 조회 완료 -----");
        int updateCnt = 0;
        for (Product product : productList) {
            // 1초에 한 상품 씩 조회합니다 (NAVER 제한)
            TimeUnit.SECONDS.sleep(1);

            // i 번째 관심 상품의 제목으로 검색을 실행합니다.
            String title = product.getTitle();
            List<ItemDto> itemDtoList = apiService.searchItems(title);
            //TODO: naver api 를 통해 리스트 조회 --> 메시지큐를 통해 미리 조회해놓기

            if (itemDtoList.size() > 0) {
                ItemDto itemDto = itemDtoList.get(0);
                // i 번째 관심 상품 정보를 업데이트합니다.
                Long id = product.getId();
                try {
                    productService.updateBySearch(id, itemDto);
                    updateCnt += 1;
                    log.info("-----i번째 상품 업데이트 완료" + title + "-----");
                } catch (Exception e) {
                    log.error(id + " : " + e.getMessage());
                }
            }else{
                log.info("-----i번째 상품 업데이트 실패(조회불가)" + title + "-----");
            }
        }
        log.info("*** 가격 업데이트 스케줄러: 총 {}건 중 {}건 실행 완료 ***", selectCnt, updateCnt);

    }

}
