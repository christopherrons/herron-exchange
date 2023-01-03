package com.herron.exchange.exchange.server.rest.orderbook;

import com.herron.exchange.exchange.server.rest.orderbook.api.OrderbookRestRequest;

public record OrderbookSnapshotRestRequest(String orderbookId) implements OrderbookRestRequest {
}
