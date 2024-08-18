package com.herron.exchange.exchangeengine.server;

import com.herron.exchange.common.api.common.api.trading.OrderbookEvent;
import com.herron.exchange.common.api.common.cache.ReferenceDataCache;
import com.herron.exchange.exchangeengine.server.shadoworderbook.ShadowOrderbookHandler;
import com.herron.exchange.exchangeengine.server.shadoworderbook.api.ShadowOrderbookReadonly;
import com.herron.exchange.exchangeengine.server.websocket.LiveEventStreamingService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ExchangeEngine {

    private final LiveEventStreamingService liveEventStreamingService;
    private final Map<String, ShadowOrderbookHandler> partitionKeyToMatchingEngine = new ConcurrentHashMap<>();
    private final Map<String, String> orderbookToPartitionKey = new ConcurrentHashMap<>();

    public ExchangeEngine(LiveEventStreamingService liveEventStreamingService) {
        this.liveEventStreamingService = liveEventStreamingService;
    }

    public void handleOrderbookEvents(OrderbookEvent orderbookEvent) {
        var orderbookId = orderbookEvent.orderbookId();
        var key = ReferenceDataCache.getCache().getOrderbookData(orderbookId).instrument().product().productName();
        orderbookToPartitionKey.putIfAbsent(orderbookId, key);
        partitionKeyToMatchingEngine.computeIfAbsent(key, k -> {
                    var orderbookHandler = new ShadowOrderbookHandler(key, liveEventStreamingService);
                    orderbookHandler.init();
                    return orderbookHandler;
                })
                .queueMessage(orderbookEvent);
    }

    public Optional<ShadowOrderbookReadonly> getOrderbook(String orderbookId) {
        return Optional.ofNullable(orderbookToPartitionKey.get(orderbookId))
                .filter(partitionKeyToMatchingEngine::containsKey)
                .map(key -> partitionKeyToMatchingEngine.get(key).getOrderbook(orderbookId));
    }
}