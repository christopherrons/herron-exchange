package com.herron.exchange.exchangeengine.server.shadoworderbook.cache;

import com.herron.exchange.common.api.common.cache.ReferenceDataCache;
import com.herron.exchange.exchangeengine.server.shadoworderbook.ShadowOrderbook;
import com.herron.exchange.exchangeengine.server.shadoworderbook.factory.OrderbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderbookCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderbookCache.class);

    private final Map<String, ShadowOrderbook> orderbookIdToOrderBook = new ConcurrentHashMap<>();

    public ShadowOrderbook getOrCreateOrderbook(String orderbookId) {
        return orderbookIdToOrderBook.computeIfAbsent(orderbookId, obId -> OrderbookFactory.createOrderbook(ReferenceDataCache.getCache().getOrderbookData(obId)));
    }
}