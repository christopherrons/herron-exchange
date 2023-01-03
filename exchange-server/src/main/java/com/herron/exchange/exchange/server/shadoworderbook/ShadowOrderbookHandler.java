package com.herron.exchange.exchange.server.shadoworderbook;

import com.herron.exchange.common.api.common.api.Order;
import com.herron.exchange.common.api.common.model.PartitionKey;
import com.herron.exchange.common.api.common.wrappers.ThreadWrapper;
import com.herron.exchange.exchange.server.shadoworderbook.api.ShadowOrderbook;
import com.herron.exchange.exchange.server.shadoworderbook.cache.OrderbookCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class ShadowOrderbookHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShadowOrderbookHandler.class);
    private final Queue<Order> orderQueue = new ConcurrentLinkedQueue<>();
    private final OrderbookCache orderbookCache;
    private final PartitionKey partitionKey;

    public ShadowOrderbookHandler(PartitionKey partitionKey, OrderbookCache orderbookCache) {
        this.partitionKey = partitionKey;
        this.orderbookCache = orderbookCache;

        var messagePollExecutorService = newSingleThreadExecutor(new ThreadWrapper(partitionKey.description()));
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

            Order order = orderQueue.peek();

            if (order == null) {
                continue;
            }

            try {
                ShadowOrderbook orderbook = orderbookCache.getOrderbook(order.orderbookId());
                if (orderbook != null) {
                    orderbook.updateOrderbook(orderQueue.poll());
                }
            } catch (Exception e) {
                LOGGER.warn("Unhandled exception for order: {}, {}", order, e);
            }
        }
    }
}
