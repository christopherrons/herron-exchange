package com.herron.exchange.exchange.server.rest.orderbook.api;

import com.herron.exchange.exchange.server.rest.api.RestRequest;

public interface OrderbookRestRequest extends RestRequest {

    String orderbookId();
}
