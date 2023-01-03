package com.herron.exchange.exchange.server.rest.orderbook;

import com.herron.exchange.exchange.server.rest.orderbook.api.OrderbookRestResponse;
import com.herron.exchange.exchange.server.shadoworderbook.model.OrderbookSnapshot;

public record OrderbookSnapshotDto(String orderbookId, OrderbookSnapshot snapshot) implements OrderbookRestResponse {
}
