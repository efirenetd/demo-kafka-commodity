package org.efire.net.common;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    //This method demonstrates how to override Refresh Rate to 3mins (metadata.max.age.ms) for Kafka Rebalancing
    //By default, 5mins
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        var props = kafkaProperties.buildProducerProperties();
        props.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, "180000");
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
