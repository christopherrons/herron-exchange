package com.herron.exchange.exchangeengine.server.websocket;

import com.herron.exchange.common.api.common.api.Message;
import com.herron.exchange.common.api.common.api.trading.Order;
import com.herron.exchange.common.api.common.messages.trading.PriceQuote;
import com.herron.exchange.common.api.common.messages.trading.StateChange;
import com.herron.exchange.common.api.common.messages.trading.TradeExecution;
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
        if (message instanceof PriceQuote priceQuote) {
            TopicAndMessage wrapper = switch (priceQuote.side()) {
                case BID -> new TopicAndMessage(String.format(TOPIC_PATTERN, BEST_BID.getTopicName(), priceQuote.orderbookId()), message);
                case ASK -> new TopicAndMessage(String.format(TOPIC_PATTERN, BEST_ASK.getTopicName(), priceQuote.orderbookId()), message);
            };
            topicAndMessages.add(wrapper);

        } else if (message instanceof TradeExecution tradeExecution) {
            tradeExecution.messages().stream()
                    .map(m -> new TopicAndMessage(String.format(TOPIC_PATTERN, ORDERBOOK_EVENT.getTopicName(), tradeExecution.orderbookId()), m))
                    .forEach(topicAndMessages::add);

        } else if (message instanceof StateChange stateChange) {
            topicAndMessages.add(new TopicAndMessage(String.format(TOPIC_PATTERN, ORDERBOOK_EVENT.getTopicName(), stateChange.orderbookId()), stateChange));
            topicAndMessages.add(new TopicAndMessage(String.format(TOPIC_PATTERN, STATE_CHANGE.getTopicName(), stateChange.orderbookId()), stateChange));

        } else if (message instanceof Order order) {
            topicAndMessages.add(new TopicAndMessage(String.format(TOPIC_PATTERN, ORDERBOOK_EVENT.getTopicName(), order.orderbookId()), order));
        }

        return topicAndMessages;
    }

    private record TopicAndMessage(String topic, Message message) {
    }
}
