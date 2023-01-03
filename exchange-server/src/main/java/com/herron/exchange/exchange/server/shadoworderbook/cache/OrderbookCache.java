package com.herron.exchange.exchange.server.shadoworderbook.cache;

import com.herron.exchange.common.api.common.api.OrderbookData;
import com.herron.exchange.common.api.common.api.StateChange;
import com.herron.exchange.exchange.server.shadoworderbook.ShadowOrderbook;
import com.herron.exchange.exchange.server.shadoworderbook.api.Orderbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderbookCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderbookCache.class);

    private final Map<String, Orderbook> orderbookIdToOrderBook = new ConcurrentHashMap<>();

    public void createOrderbook(OrderbookData orderbookData) {
        orderbookIdToOrderBook.computeIfAbsent(orderbookData.orderbookId(), k -> new ShadowOrderbook(orderbookData));
    }

    public Orderbook getOrderbook(String orderbookId) {
        if (orderbookIdToOrderBook.containsKey(orderbookId)) {
            return orderbookIdToOrderBook.get(orderbookId);
        }

        LOGGER.warn("Cannot find orderbook for orderbookid: {}", orderbookId);
        return null;

    }

    public void updateState(StateChange stateChange) {
        if (orderbookIdToOrderBook.containsKey(stateChange.orderbookId())) {
            orderbookIdToOrderBook.get(stateChange.orderbookId()).updateState(stateChange);
        } else {
            LOGGER.warn("Cannot update state due to missing orderbook: {}", stateChange);
        }
    }

}