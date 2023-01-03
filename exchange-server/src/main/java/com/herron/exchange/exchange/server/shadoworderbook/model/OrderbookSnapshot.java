package com.herron.exchange.exchange.server.shadoworderbook.model;

import com.herron.exchange.common.api.common.api.Order;

import java.util.List;

public record OrderbookSnapshot(long sequenceNumber, List<Order> bidOrders, List<Order> askOrders) {
}
