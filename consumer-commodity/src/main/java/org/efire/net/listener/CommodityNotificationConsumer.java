package org.efire.net.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.efire.net.entity.Commodity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommodityNotificationConsumer {

    private ObjectMapper objectMapper;

    public CommodityNotificationConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "commodity-input-topic", groupId = "cg-notification", concurrency = "3")
    public void onMessage(ConsumerRecord<String, String> record) throws JsonProcessingException, InterruptedException {
        var message = record.value();
        var commodity = objectMapper.readValue(message, Commodity.class);
        // Even there is a slow process, the consumer in not blocked.
        //Thread.sleep(ThreadLocalRandom.current().nextLong(500, 1000));

        log.info("Notification logic, Partition: {}, OffSet: {}, for {} ",
                record.partition(), record.offset(), commodity);
    }
}
