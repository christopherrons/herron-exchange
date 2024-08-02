package com.herron.exchange.exchangeengine.server.websocket;

import com.herron.exchange.common.api.common.api.Message;
import com.herron.exchange.common.api.common.api.trading.Order;
import com.herron.exchange.common.api.common.messages.trading.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashSet;
import java.util.Set;

import static com.herron.exchange.exchangeengine.server.websocket.ExchangeWebsocketTopics.*;

public class LiveEventStreamingService {
    private static final String TOPIC_PATTERN = "/topic/%s/%s";
    private static final Logger LOGGER = LoggerFactory.getLogger(LiveEventStreamingService.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final SubscriptionService subscriptionService;

    public LiveEventStreamingService(SimpMessagingTemplate messagingTemplate, SubscriptionService subscriptionService) {
        this.messagingTemplate = messagingTemplate;
        this.subscriptionService = subscriptionService;
    }

    public void streamMessage(Message message) {
        generateTopics(message).stream()
                .filter(topicAndMessage -> subscriptionService.hasSubscribers(topicAndMessage.topic()))
                .forEach(topicAndMessage -> messagingTemplate.convertAndSend(topicAndMessage.topic(), topicAndMessage.message()));
    }

    private Set<TopicAndMessage> generateTopics(Message message) {
        var topicAndMessages = new HashSet<TopicAndMessage>();

        switch (message) {
            case TopOfBook topOfBook -> {
                topicAndMessages.add(new TopicAndMessage(
                        String.format(TOPIC_PATTERN, TOP_OF_BOOK.getTopicName(), topOfBook.orderbookId()), topOfBook));
            }
            case TradeExecution tradeExecution -> {
                for (var m : tradeExecution.messages()) {
                    topicAndMessages.add(new TopicAndMessage(
                            String.format(TOPIC_PATTERN, ORDERBOOK_EVENT.getTopicName(), tradeExecution.orderbookId()), m));
                    if (m instanceof Trade trade) {
                        topicAndMessages.add(new TopicAndMessage(
                                String.format(TOPIC_PATTERN, TRADES.getTopicName(), tradeExecution.orderbookId()), trade));
                    }
                }
            }
            case StateChange stateChange -> {
                topicAndMessages.add(new TopicAndMessage(
                        String.format(TOPIC_PATTERN, ORDERBOOK_EVENT.getTopicName(), stateChange.orderbookId()), stateChange));
                topicAndMessages.add(new TopicAndMessage(
                        String.format(TOPIC_PATTERN, STATE_CHANGE.getTopicName(), stateChange.orderbookId()), stateChange));
            }
            case Order order -> {
                topicAndMessages.add(new TopicAndMessage(
                        String.format(TOPIC_PATTERN, ORDERBOOK_EVENT.getTopicName(), order.orderbookId()), order));
            }
            case MarketByLevel marketByLevel -> {
                topicAndMessages.add(new TopicAndMessage(
                        String.format(TOPIC_PATTERN, MARKET_BY_LEVEL.getTopicName(), marketByLevel.orderbookId()), marketByLevel));
            }
            default -> LOGGER.warn("Unsupported message type: " + message.getClass().getName());
        }

        return topicAndMessages;
    }

    private record TopicAndMessage(String topic, Message message) {
    }
}
