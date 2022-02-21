package org.efire.net.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.efire.net.entity.Image;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImageConsumer {

    private ObjectMapper objectMapper;

    public ImageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "T-IMAGE", concurrency = "3")
    public void onMessage(ConsumerRecord<String, String> record) throws JsonProcessingException {
        log.info("Received payload: {} ", record);
        var image = objectMapper.readValue(record.value(), Image.class);
        if (image.getType().equalsIgnoreCase("png")) {
            throw new RecoverableDataAccessException("Cannot handle image type: "+image.getType().toUpperCase());
        }
        log.info("Processing image: {}", image.getType().toUpperCase());
    }
}
