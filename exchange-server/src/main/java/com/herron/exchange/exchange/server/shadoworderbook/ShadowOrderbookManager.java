package com.herron.exchange.exchange.server.shadoworderbook;

import com.herron.exchange.common.api.common.api.trading.orders.Order;
import com.herron.exchange.exchange.server.rest.orderbook.OrderbookSnapshotDto;
import com.herron.exchange.exchange.server.rest.orderbook.OrderbookSnapshotRestRequest;
import com.herron.exchange.exchange.server.rest.orderbook.api.OrderbookRestRequest;
import com.herron.exchange.exchange.server.rest.orderbook.api.OrderbookRestResponse;
import com.herron.exchange.exchange.server.shadoworderbook.api.ShadowOrderbook;
import com.herron.exchange.exchange.server.shadoworderbook.cache.OrderbookCache;
import com.herron.exchange.exchange.server.websocket.WebSocketDataStreamManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class ShadowOrderbookManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShadowOrderbookManager.class);
    private final Queue<Order> orderQueue = new ConcurrentLinkedQueue<>();
    private final OrderbookCache orderbookCache;
    private final WebSocketDataStreamManager dataStreamManager;

    public ShadowOrderbookManager(OrderbookCache orderbookCache, WebSocketDataStreamManager dataStreamManager) {
        this.orderbookCache = orderbookCache;
        this.dataStreamManager = dataStreamManager;

        var messagePollExecutorService = newSingleThreadExecutor();
        messagePollExecutorService.execute(this::pollMessages);
    }

    public void queueMessage(Order message) {
        orderQueue.add(message);
    }

    private void pollMessages() {
        while (true) {

            if (orderQueue.isEmpty()) {
                continue;
            }

            if (orderQueue.peek() == null) {
                continue;
            }

            Order order = orderQueue.poll();
            try {
                ShadowOrderbook orderbook = orderbookCache.getOrderbook(order.orderbookId());
                if (orderbook != null) {
                    long latestSequenceNumber = orderbook.updateOrderbook(order);
                    dataStreamManager.publishOrder(latestSequenceNumber, order);
                }
            } catch (Exception e) {
                LOGGER.warn("Unhandled exception for order: {}, {}", order, e);
            }
        }
    }

    public OrderbookRestResponse routeRequest(OrderbookRestRequest orderbookRequest) {
        ShadowOrderbook orderbook = orderbookCache.getOrderbook(orderbookRequest.orderbookId());
        if (orderbookRequest instanceof OrderbookSnapshotRestRequest) {
            return new OrderbookSnapshotDto(orderbookRequest.orderbookId(), orderbook.getOrderbookSnapshot());
        }
        return null;
    }
}
