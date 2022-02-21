package org.efire.net.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.efire.net.entity.Image;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Service
@Slf4j
public class ImageProducer {

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    public ImageProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public ListenableFuture<SendResult<String, String>> sendImage(Image image) {
        try {
            var imagePayload = objectMapper.writeValueAsString(image);
            var producerRecord = new ProducerRecord<>("T-IMAGE", image.getName(), imagePayload);
            var futureResult = kafkaTemplate.send(producerRecord);
            futureResult.addCallback(new KafkaSendCallback<>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    handleSuccess(image.getType(), imagePayload, result);
                }

                @Override
                public void onFailure(KafkaProducerException ex) {
                    ProducerRecord<String, String> failed = ex.getFailedProducerRecord();
                    handleFailure(failed, ex);
                }
            });
            return futureResult;
        } catch (JsonProcessingException e) {
           throw new RuntimeException(e);
        }
    }

    private void handleFailure(ProducerRecord<String, String> failed, KafkaProducerException ex) {
        LocalDateTime triggerTime =
                LocalDateTime.ofInstant(Instant.ofEpochSecond(failed.timestamp()),
                        TimeZone.getDefault().toZoneId());
        log.error("Error Sending the Message to topic {} and the exception is {} \n" +
                        "Payload: \n {} \n" +
                        "Triggered Date/Time: {} \n"
                , failed.topic(), ex.getMessage(), failed.value(), triggerTime);
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error in OnFailure: {}", throwable.getMessage());
        }
    }

    private void handleSuccess(String name, String imagePayload, SendResult<String, String> result) {
        log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}",
                name, imagePayload, result.getRecordMetadata().partition());
    }
}
