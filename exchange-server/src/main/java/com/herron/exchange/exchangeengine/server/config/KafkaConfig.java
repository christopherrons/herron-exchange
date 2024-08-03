package com.herron.exchange.exchangeengine.server.config;

import com.herron.exchange.common.api.common.api.MessageFactory;
import com.herron.exchange.common.api.common.enums.KafkaTopicEnum;
import com.herron.exchange.common.api.common.kafka.KafkaConsumerClient;
import com.herron.exchange.common.api.common.kafka.model.KafkaSubscriptionDetails;
import com.herron.exchange.common.api.common.messages.common.PartitionKey;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConfig {
    private static final String GROUP_ID = "exchange-engine";

    @Bean
    public KafkaConsumerClient kafkaConsumerClient(MessageFactory messageFactory, ConsumerFactory<String, String> consumerFactor) {
        return new KafkaConsumerClient(messageFactory, consumerFactor);
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Component
    @ConfigurationProperties(prefix = "kafka.consumer")
    public static class KafkaConsumerConfig {

        private List<KafkaTopicConfig> config;

        public List<KafkaTopicConfig> getConfig() {
            return config;
        }

        public void setConfig(List<KafkaTopicConfig> config) {
            this.config = config;
        }

        List<KafkaSubscriptionDetails> getDetails(KafkaTopicEnum topicEnum) {
            return config.stream()
                    .filter(c -> c.topic.equals(topicEnum.getTopicName()))
                    .map(c -> new KafkaSubscriptionDetails(GROUP_ID, new PartitionKey(topicEnum, c.partition), c.offset, c.eventLogging))
                    .toList();
        }

        public record KafkaTopicConfig(int offset,
                                       int partition,
                                       int eventLogging,
                                       String topic) {
        }
    }
}
