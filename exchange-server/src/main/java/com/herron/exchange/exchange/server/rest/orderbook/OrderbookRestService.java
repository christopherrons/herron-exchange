package com.herron.exchange.exchange.server.rest.orderbook;

import com.herron.exchange.exchange.server.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderbookRestService {

    @Autowired
    private Exchange exchange;

    public OrderbookSnapshotDto getOrderbookSnapshot(OrderbookSnapshotRestRequest orderbookSnapshotRestRequest) {
        return (OrderbookSnapshotDto) exchange.routeRequest(orderbookSnapshotRestRequest);
    }
}
