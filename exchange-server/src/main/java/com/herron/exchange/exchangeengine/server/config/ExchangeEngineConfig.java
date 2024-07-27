package com.herron.exchange.exchangeengine.server.config;

import com.herron.exchange.common.api.common.api.MessageFactory;
import com.herron.exchange.common.api.common.kafka.KafkaConsumerClient;
import com.herron.exchange.common.api.common.mapping.DefaultMessageFactory;
import com.herron.exchange.exchangeengine.server.ExchangeEngine;
import com.herron.exchange.exchangeengine.server.ExchangeEngineBootloader;
import com.herron.exchange.exchangeengine.server.consumers.AuditTrailConsumer;
import com.herron.exchange.exchangeengine.server.consumers.ReferenceDataConsumer;
import com.herron.exchange.exchangeengine.server.consumers.TopOfBookConsumer;
import com.herron.exchange.exchangeengine.server.websocket.LiveEventStreamingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.herron.exchange.common.api.common.enums.KafkaTopicEnum.*;

@Configuration
public class ExchangeEngineConfig {

    @Bean
    public MessageFactory messageFactory() {
        return new DefaultMessageFactory();
    }

    @Bean
    public ExchangeEngine exchangeEngine(LiveEventStreamingService liveEventStreamingService) {
        return new ExchangeEngine(liveEventStreamingService);
    }

    @Bean
    public ReferenceDataConsumer referenceDataConsumer(KafkaConsumerClient kafkaConsumerClient, KafkaConfig.KafkaConsumerConfig config) {
        return new ReferenceDataConsumer(kafkaConsumerClient, config.getDetails(REFERENCE_DATA));
    }

    @Bean
    public TopOfBookConsumer topOfBookConsumer(ExchangeEngine exchangeEngine, KafkaConsumerClient kafkaConsumerClient, KafkaConfig.KafkaConsumerConfig config) {
        return new TopOfBookConsumer(exchangeEngine, kafkaConsumerClient, config.getDetails(TOP_OF_BOOK_QUOTE));
    }

    @Bean
    public AuditTrailConsumer auditTrailConsumer(ExchangeEngine pricingEngine, KafkaConsumerClient kafkaConsumerClient, KafkaConfig.KafkaConsumerConfig config) {
        return new AuditTrailConsumer(pricingEngine, kafkaConsumerClient, config.getDetails(AUDIT_TRAIL));
    }

    @Bean(initMethod = "init")
    public ExchangeEngineBootloader exchangeEngineBootloader(ReferenceDataConsumer referenceDataConsumer,
                                                             TopOfBookConsumer topOfBookConsumer,
                                                             AuditTrailConsumer auditTrailConsumer) {
        return new ExchangeEngineBootloader(referenceDataConsumer, topOfBookConsumer, auditTrailConsumer);
    }
}
