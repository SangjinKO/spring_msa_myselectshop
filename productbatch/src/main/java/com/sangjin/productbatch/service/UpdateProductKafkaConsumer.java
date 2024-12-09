package com.sangjin.productbatch.service;

import com.sangjin.product.dto.ItemDto;
import com.sangjin.product.service.ApiService;
import com.sangjin.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "Scheduler")
@Component
@RequiredArgsConstructor
public class UpdateProductKafkaConsumer {

    @Qualifier("com.sangjin.product.infrastructure.ApiClient")
    private final ApiService apiService;
    private final ProductService productService;

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;
    private static final String KAFKA_TOPIC = "UPDATE_PRODUCT_KAFKA";

    @KafkaListener(topics = KAFKA_TOPIC, groupId = "UPDATE_PRODUCT_PRICE_CONSUMER")
    public void consumeUpdatePriceKafka(ConsumerRecord<Long, String> record) throws InterruptedException {
        Long productId = record.key();
        String productSearchTitle = record.value();
        log.info("kafka consumer consumed:" + productId.toString() + productSearchTitle);

        TimeUnit.SECONDS.sleep(1);// 1초에 한 상품 씩 조회합니다 (NAVER 제한)
        List<ItemDto> itemDtoList = apiService.searchItems(productSearchTitle);

        if (itemDtoList.size() > 0) {
            ItemDto itemDto = itemDtoList.get(0);
            try {
                productService.updateBySearch(productId, itemDto);
                log.info("-----i번째 상품 업데이트 완료" + productId.toString() + productSearchTitle + "-----");
            } catch (Exception e) {
                log.error(productId + " : " + e.getMessage());
            }
        }else{
            log.info("-----i번째 상품 업데이트 실패(조회불가)" + productId.toString() + productSearchTitle + "-----");
        }
    }
}
