package com.herron.exchange.exchangeengine.server.websocket;

public enum ExchangeWebsocketTopics {
    TRADES("trades"),
    BEST_BID("bestBid"),
    TOP_OF_BOOK("topOfBook"),
    BEST_ASK("bestAsk"),
    STATE_CHANGE("stateChange"),
    MARKET_BY_LEVEL("marketByLevel"),
    ORDERBOOK_EVENT("orderbookEvent");

    private final String topicName;

    ExchangeWebsocketTopics(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}
