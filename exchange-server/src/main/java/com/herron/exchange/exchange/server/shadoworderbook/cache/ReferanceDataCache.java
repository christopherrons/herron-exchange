package com.herron.exchange.exchange.server.shadoworderbook.cache;


import com.herron.exchange.common.api.common.api.referencedata.instruments.Instrument;
import com.herron.exchange.common.api.common.api.referencedata.orderbook.OrderbookData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReferanceDataCache {

    private final Map<String, OrderbookData> orderbookIdToOrderBookData = new ConcurrentHashMap<>();
    private final Map<String, Instrument> orderbookIdToInstrument = new ConcurrentHashMap<>();

    public void addOrderbookData(OrderbookData orderbookData) {
        orderbookIdToOrderBookData.put(orderbookData.orderbookId(), orderbookData);
    }

    public OrderbookData getOrderbookData(String orderbookId) {
        return orderbookIdToOrderBookData.get(orderbookId);
    }

    public void addInstrument(Instrument instrument) {
        orderbookIdToInstrument.put(instrument.instrumentId(), instrument);
    }

    public Instrument getInstrument(String instrumentId) {
        return orderbookIdToInstrument.get(instrumentId);
    }
}