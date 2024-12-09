package com.sangjin.product.service;

import com.sangjin.product.repository.Product;
import com.sangjin.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j(topic = "Scheduler")
@Component
@RequiredArgsConstructor
public class UpdateProductKafkaProducer {

    private final ProductRepository productRepository;

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;
    private static final String KAFKA_TOPIC = "UPDATE_PRODUCT_KAFKA";

    public void updatePriceKafka() throws InterruptedException {
        log.info("*** 가격 업데이트 카프카 실행 ***");
        List<Product> productList = productRepository.findAll();
        int selectCnt = productList.size();
        log.info("-----저장된 모든 상품 조회 완료 -----");
        int updateCnt = 0;
        for (Product product : productList) {
//            TimeUnit.SECONDS.sleep(1);// 1초에 한 상품 씩 조회합니다 (NAVER 제한)
            // i 번째 관심 상품의 제목으로 검색을 실행합니다.
            String title = product.getTitle();
            Long id = product.getId();
            kafkaTemplate.send(KAFKA_TOPIC, id, title);
            updateCnt += 1;
            log.info("-----i번째 상품 메시지 완료: ID & Title: " + id + title + "-----");
        }
        log.info("*** 가격 업데이트: 총 {}건 중 {}건 카프카 요청 완료 ***", selectCnt, updateCnt);
    }

//    @KafkaListener(topics = KAFKA_TOPIC, groupId = "UPDATE_PRODUCT_PRICE_CONSUMER")
//    public void consumeUpdatePriceKafka(ConsumerRecord<Long, String> record) throws InterruptedException {
//        Long productId = record.key();
//        String productSearchTitle = record.value();
//        log.info("kafka consumer consumed:" + productId.toString() + productSearchTitle);
//
//        TimeUnit.SECONDS.sleep(1);// 1초에 한 상품 씩 조회합니다 (NAVER 제한)
//        List<ItemDto> itemDtoList = apiService.searchItems(productSearchTitle);
//
//        if (itemDtoList.size() > 0) {
//            ItemDto itemDto = itemDtoList.get(0);
//            try {
//                productService.updateBySearch(productId, itemDto);
//                log.info("-----i번째 상품 업데이트 완료" + productId.toString() + productSearchTitle + "-----");
//            } catch (Exception e) {
//                log.error(productId + " : " + e.getMessage());
//            }
//        }else{
//            log.info("-----i번째 상품 업데이트 실패(조회불가)" + productId.toString() + productSearchTitle + "-----");
//        }
//    }
}
