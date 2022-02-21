package org.efire.net.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.efire.net.common.CommodityProperties;
import org.efire.net.entity.Commodity;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
@Slf4j
public class CommodityProducer {

    private KafkaTemplate<String, String> kafkaTemplate;
    private CommodityProperties commodityProps;
    private ObjectMapper objectMapper;

    public CommodityProducer(KafkaTemplate<String, String> kafkaTemplate, CommodityProperties commodityProps, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.commodityProps = commodityProps;
        this.objectMapper = objectMapper;
    }

    public ListenableFuture<SendResult<String, String>> sendCommodityEvent(Commodity commodity) throws JsonProcessingException {
        var commodityJson = objectMapper.writeValueAsString(commodity);
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(commodityProps.getInputTopic(), commodity.getName(), commodityJson);
        ListenableFuture<SendResult<String, String>> futureResult = this.kafkaTemplate.send(producerRecord);
        futureResult.addCallback( new KafkaSendCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Message successfully sent in Topic: {}, Partition: {}, OffSet: {} with Key: {} and Value {}",
                        result.getProducerRecord().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        result.getProducerRecord().key(),
                        result.getProducerRecord().value());
            }

            @Override
            public void onFailure(KafkaProducerException e) {
                log.error("Error in sending the message. Details: {}", e.getStackTrace());
            }
        });
        return futureResult;
    }
}
