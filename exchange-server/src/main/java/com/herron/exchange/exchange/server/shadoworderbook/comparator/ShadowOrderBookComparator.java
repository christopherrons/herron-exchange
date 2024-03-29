package com.herron.exchange.exchange.server.shadoworderbook.comparator;


import com.herron.exchange.common.api.common.api.trading.orders.Order;

import java.util.Comparator;

public class ShadowOrderBookComparator implements Comparator<Order> {
    @Override
    public int compare(Order order, Order otherEvent) {
        if (order.timeOfEventMs() < otherEvent.timeOfEventMs()) {
            return -1;
        } else {
            return order.orderId().equals(otherEvent.orderId()) ? 0 : 1;
        }

    }

}