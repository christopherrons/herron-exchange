package com.herron.exchange.exchangeengine.server.rest;

import com.herron.exchange.common.api.common.api.referencedata.orderbook.OrderbookData;
import com.herron.exchange.common.api.common.cache.ReferenceDataCache;
import com.herron.exchange.common.api.common.enums.TradingStatesEnum;
import com.herron.exchange.exchangeengine.server.ExchangeEngine;

import java.util.Set;
import java.util.stream.Collectors;

public class ExchangeDataService {
    private final ExchangeEngine exchangeEngine;

    public ExchangeDataService(ExchangeEngine exchangeEngine) {
        this.exchangeEngine = exchangeEngine;
    }

    public Set<String> getOrderbookIds() {
        return ReferenceDataCache.getCache().getOrderbookData().stream().map(OrderbookData::orderbookId).collect(Collectors.toSet());
    }

    public TradingStatesEnum getOrderbookState(String orderbookId) {
        return exchangeEngine.getOrderbook(orderbookId).getState();
    }
}
