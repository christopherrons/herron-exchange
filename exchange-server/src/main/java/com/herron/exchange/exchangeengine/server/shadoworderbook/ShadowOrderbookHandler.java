package com.herron.exchange.exchangeengine.server.shadoworderbook;

import com.herron.exchange.common.api.common.api.trading.Order;
import com.herron.exchange.common.api.common.api.trading.OrderbookEvent;
import com.herron.exchange.common.api.common.messages.trading.StateChange;
import com.herron.exchange.common.api.common.wrappers.ThreadWrapper;
import com.herron.exchange.exchangeengine.server.shadoworderbook.api.ShadowOrderbookReadonly;
import com.herron.exchange.exchangeengine.server.shadoworderbook.cache.OrderbookCache;
import com.herron.exchange.exchangeengine.server.websocket.LiveEventStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ShadowOrderbookHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShadowOrderbookHandler.class);
    private final BlockingQueue<OrderbookEvent> eventQueue = new LinkedBlockingDeque<>();
    private final OrderbookCache orderbookCache = new OrderbookCache();
    private final LiveEventStreamingService liveEventStreamingService;

    private final ExecutorService service;
    private final String id;
    private final AtomicBoolean isShadowing = new AtomicBoolean(false);

    public ShadowOrderbookHandler(String id, LiveEventStreamingService liveEventStreamingService) {
        this.id = id;
        this.liveEventStreamingService = liveEventStreamingService;
        service = Executors.newSingleThreadExecutor(new ThreadWrapper(id));
    }

    public void init() {
        isShadowing.set(true);
        service.execute(this::startShadowing);
    }

    public void queueMessage(OrderbookEvent orderbookEvent) {
        var orderbook = orderbookCache.getOrCreateOrderbook(orderbookEvent.orderbookId());
        if (orderbook == null) {
            LOGGER.error("Orderbook {} does not exist, queue orderbook event {}.", orderbookEvent.orderbookId(), orderbookEvent);
            return;
        }
        eventQueue.add(orderbookEvent);
    }

    private void startShadowing() {
        LOGGER.info("Starting shadowing orderbook {}.", id);
        OrderbookEvent orderbookEvent;
        while (isShadowing.get() || !eventQueue.isEmpty()) {

            orderbookEvent = poll();
            if (orderbookEvent == null) {
                continue;
            }

            try {
                handleEvent(orderbookEvent);
            } catch (Exception e) {
                LOGGER.warn("Unhandled exception for orderbookEvent: {}", orderbookEvent, e);
            }
        }
    }

    private OrderbookEvent poll() {
        try {
            return eventQueue.poll(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    private void handleEvent(OrderbookEvent orderbookEvent) {
        streamEvent(orderbookEvent);
        var orderbook = orderbookCache.getOrCreateOrderbook(orderbookEvent.orderbookId());
        switch (orderbookEvent) {
            case StateChange stateChange -> orderbook.updateState(stateChange.tradeState());
            case Order order -> orderbook.updateOrderbook(order);
            default -> {
            }
        }
    }

    public ShadowOrderbookReadonly getOrderbook(String orderbookId) {
        return orderbookCache.getOrCreateOrderbook(orderbookId);
    }

    private void streamEvent(OrderbookEvent orderbookEvent) {
        liveEventStreamingService.streamMessage(orderbookEvent);
    }

}
