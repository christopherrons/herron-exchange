package com.herron.exchange.exchangeengine.server.rest;

import com.herron.exchange.common.api.common.api.referencedata.orderbook.OrderbookData;
import com.herron.exchange.common.api.common.cache.ReferenceDataCache;
import com.herron.exchange.common.api.common.enums.TradingStatesEnum;
import com.herron.exchange.common.api.common.messages.refdata.InstrumentHierarchy;
import com.herron.exchange.exchangeengine.server.ExchangeEngine;
import com.herron.exchange.exchangeengine.server.shadoworderbook.api.ShadowOrderbookReadonly;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExchangeDataService {
    private final ExchangeEngine exchangeEngine;

    public ExchangeDataService(ExchangeEngine exchangeEngine) {
        this.exchangeEngine = exchangeEngine;
    }

    public Set<String> getOrderbookIds() {
        return ReferenceDataCache.getCache().getOrderbookData().stream().map(OrderbookData::orderbookId).collect(Collectors.toSet());
    }

    public TradingStatesEnum getOrderbookState(String orderbookId) {
        return exchangeEngine.getOrderbook(orderbookId).map(ShadowOrderbookReadonly::getState).orElse(TradingStatesEnum.CLOSED);
    }

    public InstrumentHierarchy getInstrumentHierarchy() {
        return InstrumentHierarchyBuilder.build(ReferenceDataCache.getCache().getOrderbookData());
    }
}
