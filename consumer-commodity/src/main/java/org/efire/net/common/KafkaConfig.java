package org.efire.net.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.efire.net.entity.Commodity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
public class KafkaConfig {

    @Autowired
    private KafkaProperties kafkaProperties;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public ConsumerFactory<Object, Object> consumerFactory() {
        var props = kafkaProperties.buildConsumerProperties();

        props.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, "120000");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> highestCommodityContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<Object, Object>();
        configurer.configure(containerFactory, consumerFactory());

        containerFactory.setRecordFilterStrategy(consumerRecord -> {
            try {
                var commodity = objectMapper.readValue(consumerRecord.value().toString(), Commodity.class);
                return commodity.getPriceInt() <= 100;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return false;
        });
        return containerFactory;
    }
}
