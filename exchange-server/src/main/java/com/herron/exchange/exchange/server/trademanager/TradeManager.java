package com.herron.exchange.exchange.server.trademanager;

import com.herron.exchange.common.api.common.api.Trade;
import com.herron.exchange.exchange.server.websocket.WebSocketDataStreamManager;

import java.util.concurrent.atomic.AtomicLong;

public class TradeManager {
    private final WebSocketDataStreamManager dataStreamManager;
    private final AtomicLong latestSequenceNumber = new AtomicLong(1);

    public TradeManager(WebSocketDataStreamManager dataStreamManager) {
        this.dataStreamManager = dataStreamManager;
    }

    public void publishTrade(Trade trade) {
        dataStreamManager.publishTrade(latestSequenceNumber.incrementAndGet(), trade);
    }
}
