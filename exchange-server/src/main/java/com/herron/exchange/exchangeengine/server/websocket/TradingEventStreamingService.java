package com.herron.exchange.exchangeengine.server.websocket;

import com.herron.exchange.common.api.common.api.trading.OrderbookEvent;
import com.herron.exchange.common.api.common.messages.trading.PriceQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static com.herron.exchange.exchangeengine.server.websocket.ExchangeWebsocketTopics.*;

public class TradingEventStreamingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradingEventStreamingService.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final SubscriptionService subscriptionService;

    public TradingEventStreamingService(SimpMessagingTemplate messagingTemplate, SubscriptionService subscriptionService) {
        this.messagingTemplate = messagingTemplate;
        this.subscriptionService = subscriptionService;
    }

    public void streamOrderbookEvents(OrderbookEvent orderbookEvent) {
        String topic = switch (orderbookEvent) {
            case PriceQuote priceQuote -> switch (priceQuote.side()) {
                case BID -> String.format("/topic/%s/%s", BEST_BID.getTopicName(), orderbookEvent.orderbookId());
                case ASK -> String.format("/topic/%s/%s", BEST_ASK.getTopicName(), orderbookEvent.orderbookId());
            };
            default -> String.format("/topic/%s/%s", ORDERBOOK_EVENT.getTopicName(), orderbookEvent.orderbookId());
        };

        if (!subscriptionService.hasSubscribers(topic)) {
            return;
        }

        messagingTemplate.convertAndSend(topic, orderbookEvent);
    }
}
