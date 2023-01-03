package com.herron.exchange.exchange.server;

import com.herron.exchange.common.api.common.api.*;
import com.herron.exchange.common.api.common.logging.EventLogger;
import com.herron.exchange.exchange.server.rest.api.RestRequest;
import com.herron.exchange.exchange.server.rest.api.RestResponse;
import com.herron.exchange.exchange.server.rest.orderbook.api.OrderbookRestRequest;
import com.herron.exchange.exchange.server.shadoworderbook.ShadowOrderbookManager;
import com.herron.exchange.exchange.server.shadoworderbook.cache.OrderbookCache;
import com.herron.exchange.exchange.server.shadoworderbook.cache.ReferanceDataCache;
import com.herron.exchange.exchange.server.trademanager.TradeManager;
import com.herron.exchange.exchange.server.websocket.WebSocketDataStreamManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public class Exchange {
    private static final Logger LOGGER = LoggerFactory.getLogger(Exchange.class);
    private final ShadowOrderbookManager shadowOrderbookManager;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final EventLogger eventLogger = new EventLogger(10000);
    private final OrderbookCache orderbookCache = new OrderbookCache();
    private final ReferanceDataCache referanceDataCache = new ReferanceDataCache();
    private final TradeManager tradeManager;

    public Exchange(KafkaTemplate<String, Object> kafkaTemplate, WebSocketDataStreamManager dataStreamManager) {
        this.kafkaTemplate = kafkaTemplate;
        this.shadowOrderbookManager = new ShadowOrderbookManager(orderbookCache, dataStreamManager);
        this.tradeManager = new TradeManager(dataStreamManager);
    }

    public void routeMessage(Message message) {
        if (message instanceof Order order) {
            shadowOrderbookManager.queueMessage(order);
        } else if (message instanceof OrderbookData orderbookData) {
            referanceDataCache.addOrderbookData(orderbookData);
            orderbookCache.createOrderbook(orderbookData);
        } else if (message instanceof Instrument instrument) {
            referanceDataCache.addInstrument(instrument);
        } else if (message instanceof StateChange stateChange) {
            orderbookCache.updateState(stateChange);
        } else if (message instanceof Trade trade) {
            tradeManager.publishTrade(trade);
        }

        eventLogger.logEvent();
    }

    public RestResponse routeRequest(RestRequest restRequest) {
        if (restRequest instanceof OrderbookRestRequest orderbookRequest) {
            return shadowOrderbookManager.routeRequest(orderbookRequest);
        }
        return null;
    }

}
