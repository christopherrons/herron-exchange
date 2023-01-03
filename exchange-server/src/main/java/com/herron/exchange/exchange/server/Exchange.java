package com.herron.exchange.exchange.server;

import com.herron.exchange.common.api.common.api.*;
import com.herron.exchange.common.api.common.enums.TopicEnum;
import com.herron.exchange.common.api.common.model.PartitionKey;
import com.herron.exchange.exchange.server.shadoworderbook.ShadowOrderbookHandler;
import com.herron.exchange.exchange.server.shadoworderbook.cache.OrderbookCache;
import com.herron.exchange.exchange.server.shadoworderbook.cache.ReferanceDataCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Exchange {
    private static final Logger LOGGER = LoggerFactory.getLogger(Exchange.class);
    private static final PartitionKey DEFAULT_PARTITION_KEY = new PartitionKey(TopicEnum.HERRON_AUDIT_TRAIL, 1);
    private final Map<PartitionKey, ShadowOrderbookHandler> partitionKeyToMatchingEngine = new ConcurrentHashMap<>();
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderbookCache orderbookCache = new OrderbookCache();
    private final ReferanceDataCache referanceDataCache = new ReferanceDataCache();

    public Exchange(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void queueMessage(Message message) {
        queueMessage(DEFAULT_PARTITION_KEY, message);
    }

    public void queueMessage(PartitionKey partitionKey, Message message) {
        if (message instanceof Order order) {
            partitionKeyToMatchingEngine.computeIfAbsent(partitionKey, k -> new ShadowOrderbookHandler(partitionKey, orderbookCache)).queueMessage(order);
        } else if (message instanceof OrderbookData orderbookData) {
            addOrderbookData(orderbookData);
        } else if (message instanceof Instrument instrument) {
            addInstrument(instrument);
        } else if (message instanceof StateChange stateChange) {
            updateState(stateChange);
        }
    }

    private void addOrderbookData(OrderbookData orderbookData) {
        referanceDataCache.addOrderbookData(orderbookData);
        orderbookCache.createOrderbook(orderbookData);
    }

    private void addInstrument(Instrument instrument) {
        referanceDataCache.addInstrument(instrument);
    }

    private void updateState(StateChange stateChange) {
        orderbookCache.updateState(stateChange);
    }
}
