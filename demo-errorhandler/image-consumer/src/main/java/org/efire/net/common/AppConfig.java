package org.efire.net.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class AppConfig {


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    //Simple retry (3) functionality with backoff period of 10sec (10_000 ms)
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory,
            KafkaOperations<String, String> kafkaOperations) {

        var factory = new ConcurrentKafkaListenerContainerFactory();
        configurer.configure(factory, kafkaConsumerFactory);
        factory.setConcurrency(3);
        //* This is simple retry sample
/*        factory.setErrorHandler((e, consumerRecord) -> {
            log.info("Error in Consumer is \n {} \n and record was \n {}", e.getMessage(), consumerRecord);
        });
        factory.setRetryTemplate(simpleRetryTemplate());*/

        //This is dead letter queue implementation -> T-IMAGE-DLT topic
        var deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(kafkaOperations,
                (record, e) -> new TopicPartition("T-IMAGE-DLT", record.partition()));
        factory.setErrorHandler( new SeekToCurrentErrorHandler(deadLetterPublishingRecoverer,
                new FixedBackOff(5_000L, 3)));

        return factory;
    }

    private RetryTemplate simpleRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(10_000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(simpleRetryPolicy());
        return retryTemplate;
    }

    private RetryPolicy simpleRetryPolicy() {
        //This retry policy is to specify which exception it will be apply
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(RecoverableDataAccessException.class, true);
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(3, retryableExceptions, true);
        return simpleRetryPolicy;
    }
}
