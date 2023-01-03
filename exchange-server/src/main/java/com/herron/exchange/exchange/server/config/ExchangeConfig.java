package com.herron.exchange.exchange.server.config;

import com.herron.exchange.exchange.server.Exchange;
import com.herron.exchange.exchange.server.adaptor.AuditTrailAdaptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class ExchangeConfig {

    @Bean
    public AuditTrailAdaptor getAuditTrailAdaptor(Exchange exchange) {
        return new AuditTrailAdaptor(exchange);
    }

    @Bean
    public Exchange exchange(KafkaTemplate<String, Object> kafkaTemplate) {
        return new Exchange(kafkaTemplate);
    }
}
