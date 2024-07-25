package com.herron.exchange.exchangeengine.server;

import com.herron.exchange.common.api.common.api.trading.OrderbookEvent;
import com.herron.exchange.exchangeengine.server.websocket.TradingEventStreamingService;

public class ExchangeEngine {

    private final TradingEventStreamingService tradingEventStreamingService;

    public ExchangeEngine(TradingEventStreamingService tradingEventStreamingService) {
        this.tradingEventStreamingService = tradingEventStreamingService;
    }

    public void handleOrderbookEvents(OrderbookEvent orderbookEvent) {
        tradingEventStreamingService.streamOrderbookEvents(orderbookEvent);
    }
}
