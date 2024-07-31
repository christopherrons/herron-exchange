package com.herron.exchange.exchangeengine.server.shadoworderbook.factory;

import com.herron.exchange.common.api.common.api.referencedata.orderbook.OrderbookData;
import com.herron.exchange.exchangeengine.server.shadoworderbook.ActiveOrders;
import com.herron.exchange.exchangeengine.server.shadoworderbook.comparator.FifoOrderBookComparator;
import com.herron.exchange.exchangeengine.server.shadoworderbook.comparator.ProRataOrderBookComparator;
import com.herron.exchange.exchangeengine.server.shadoworderbook.ShadowOrderbook;

public class OrderbookFactory {

    public static ShadowOrderbook createOrderbook(OrderbookData orderbookData) {
        return switch (orderbookData.matchingAlgorithm()) {
            case FIFO -> new ShadowOrderbook(orderbookData, new ActiveOrders(new FifoOrderBookComparator()));
            case PRO_RATA -> new ShadowOrderbook(orderbookData, new ActiveOrders(new ProRataOrderBookComparator()));
        };
    }
}
