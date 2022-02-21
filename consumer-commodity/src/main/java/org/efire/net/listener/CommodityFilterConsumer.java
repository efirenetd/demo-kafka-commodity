package org.efire.net.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.efire.net.entity.Commodity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommodityFilterConsumer {

    private ObjectMapper objectMapper;

    public CommodityFilterConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "commodity-input-filter-topic", groupId = "cg-all-commodity")
    public void onAllMessage(String message) throws JsonProcessingException {
        var commodity = objectMapper.readValue(message, Commodity.class);
        log.info("onAllMessage : {}", commodity);
    }

    @KafkaListener(topics = "commodity-input-filter-topic",
            groupId = "cg-highest-commodity",
            containerFactory = "highestCommodityContainerFactory")
    public void onHighestMessage(String message) throws JsonProcessingException {
        var commodity = objectMapper.readValue(message, Commodity.class);
        log.info("onHighestMessage : {}", commodity);
    }

}
