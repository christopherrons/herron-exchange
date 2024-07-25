package com.herron.exchange.exchangeengine.server.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SubscriptionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionService.class);
    private final Map<String, Set<String>> topicToSubscribers = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> subscriberToTopic = new ConcurrentHashMap<>();

    public void addSubscriber(String topic, String sessionId) {
        topicToSubscribers.computeIfAbsent(topic, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        subscriberToTopic.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(topic);
        LOGGER.info("Subscriber {} added to topic {}", sessionId, topic);
    }

    public void removeSession(String sessionId) {
        Set<String> topics = subscriberToTopic.remove(sessionId);
        if (topics != null) {
            topics.forEach(topic -> removeSubscriber(topic, sessionId));
        }
        LOGGER.info("Session {} removed.", sessionId);
    }

    public void removeSubscriber(String topic, String sessionId) {
        Set<String> subscribers = topicToSubscribers.get(topic);
        if (subscribers != null) {
            subscribers.remove(sessionId);
            LOGGER.info("Subscriber {}, removed for topic {}", sessionId, topic);
            if (subscribers.isEmpty()) {
                topicToSubscribers.remove(topic);
                LOGGER.info("No subscribers left for topic {}.", topic);
            }
        }

        Set<String> subscriptions = subscriberToTopic.get(sessionId);
        if (subscriptions != null) {
            subscriptions.remove(topic);
            if (subscriptions.isEmpty()) {
                subscriberToTopic.remove(sessionId);
            }
        }
    }

    public boolean hasSubscribers(String topic) {
        return topicToSubscribers.containsKey(topic);
    }
}

