package org.efire.net.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.efire.net.entity.Image;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageProducerTests {

    public static final String T_IMAGE = "T-IMAGE";
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    private ImageProducer imageProducer;

    @Test
    void shouldSendImageMessageReturnSuccess() throws JsonProcessingException {
        var recordMetadata = new RecordMetadata(new TopicPartition(T_IMAGE, 1), 0, 1, System.currentTimeMillis(), 0L, 0, 0);
        var jpg = Image.builder("jpg").build();
        var jpgImage = objectMapper.writeValueAsString(jpg);
        var jpgProducerRecord = new ProducerRecord<>(T_IMAGE, jpg.getName(), jpgImage);
        var jpgSendResult = new SendResult<>(jpgProducerRecord, recordMetadata);
        var jpgListenableFuture = new SettableListenableFuture();
        jpgListenableFuture.set(jpgSendResult);

        when(this.kafkaTemplate.send(jpgProducerRecord)).thenReturn(jpgListenableFuture);
        var jpgActualListenableFuture = imageProducer.sendImage(jpg);

        var png = Image.builder("png").build();
        var pngImage = objectMapper.writeValueAsString(png);
        var pngProducerRecord = new ProducerRecord<>(T_IMAGE, png.getName(), pngImage);
        var pngSendResult = new SendResult<>(pngProducerRecord, recordMetadata);
        var pngListenableFuture = new SettableListenableFuture();
        pngListenableFuture.set(pngSendResult);

        when(this.kafkaTemplate.send(pngProducerRecord)).thenReturn(pngListenableFuture);
        var pngActualListenableFuture = imageProducer.sendImage(png);

        assert jpgActualListenableFuture.isDone();
        assert pngActualListenableFuture.isDone();
    }
}
