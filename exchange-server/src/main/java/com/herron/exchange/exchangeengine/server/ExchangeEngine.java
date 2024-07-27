package com.herron.exchange.exchangeengine.server;

import com.herron.exchange.common.api.common.api.trading.OrderbookEvent;
import com.herron.exchange.exchangeengine.server.websocket.LiveEventStreamingService;

public class ExchangeEngine {

    private final LiveEventStreamingService liveEventStreamingService;

    public ExchangeEngine(LiveEventStreamingService liveEventStreamingService) {
        this.liveEventStreamingService = liveEventStreamingService;
    }

    public void handleOrderbookEvents(OrderbookEvent orderbookEvent) {
        liveEventStreamingService.streamMessage(orderbookEvent);
    }
}
