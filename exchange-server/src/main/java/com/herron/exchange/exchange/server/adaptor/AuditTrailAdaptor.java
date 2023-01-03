package com.herron.exchange.exchange.server.adaptor;

import com.herron.exchange.common.api.common.api.BroadcastMessage;
import com.herron.exchange.common.api.common.api.Message;
import com.herron.exchange.common.api.common.enums.KafkaTopicEnum;
import com.herron.exchange.common.api.common.model.PartitionKey;
import com.herron.exchange.exchange.server.Exchange;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static com.herron.exchange.common.api.common.enums.MessageTypesEnum.deserializeMessage;

public class AuditTrailAdaptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditTrailAdaptor.class);
    private static final PartitionKey PARTITION_ZERO_KEY = new PartitionKey(KafkaTopicEnum.HERRON_AUDIT_TRAIL, 0);
    private static final PartitionKey PARTITION_ONE_KEY = new PartitionKey(KafkaTopicEnum.HERRON_AUDIT_TRAIL, 1);
    private final Exchange exchange;
    private final Map<PartitionKey, AtomicLong> partitionToSequenceNumberHandler = new ConcurrentHashMap<>();

    public AuditTrailAdaptor(Exchange exchange) {
        this.exchange = exchange;
    }

    @KafkaListener(id = "audit-trail-listener-one", topicPartitions = {@TopicPartition(topic = "herron-audit-trail",
            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))})
    public void listenAuditTrailOne(ConsumerRecord<String, String> consumerRecord) {
        queueMessage(consumerRecord, PARTITION_ZERO_KEY);
    }

    @KafkaListener(id = "audit-trail-listener-two", topicPartitions = {@TopicPartition(topic = "herron-audit-trail",
            partitionOffsets = @PartitionOffset(partition = "1", initialOffset = "0"))})
    public void listenAuditTrailTwo(ConsumerRecord<String, String> consumerRecord) {
        queueMessage(consumerRecord, PARTITION_ONE_KEY);
    }

    private void queueMessage(ConsumerRecord<String, String> consumerRecord, PartitionKey partitionKey) {
        BroadcastMessage broadcastMessage = (BroadcastMessage) deserializeMessage(consumerRecord.key(), consumerRecord.value());
        if (broadcastMessage == null || broadcastMessage.serialize().isEmpty()) {
            LOGGER.warn("Unable to map message: {}", consumerRecord);
            return;
        }

        long expected = getSequenceNumber(partitionKey);
        if (broadcastMessage.sequenceNumber() != expected) {
            LOGGER.warn("GAP detected: Expected={}, Incoming={}", expected, broadcastMessage.sequenceNumber());
        }

        try {
            Message message = broadcastMessage.message();
            exchange.routeMessage(message);
        } catch (Exception e) {
            LOGGER.warn("Unhandled exception for record: {}, decoded-message: {}, {}", consumerRecord, broadcastMessage, e);
        }
    }

    private long getSequenceNumber(PartitionKey partitionKey) {
        return partitionToSequenceNumberHandler.computeIfAbsent(partitionKey, k -> new AtomicLong(1)).getAndIncrement();
    }

}
