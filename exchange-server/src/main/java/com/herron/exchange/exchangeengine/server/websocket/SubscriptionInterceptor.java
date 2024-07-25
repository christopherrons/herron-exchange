package com.herron.exchange.exchangeengine.server.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

public class SubscriptionInterceptor implements ChannelInterceptor {

    private final SubscriptionService subscriptionService;

    public SubscriptionInterceptor(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() != null) {
            StompCommand command = accessor.getCommand();
            String sessionId = accessor.getSessionId();

            switch (command) {
                case SUBSCRIBE -> {
                    String subscribeTopic = accessor.getDestination();
                    subscriptionService.addSubscriber(subscribeTopic, sessionId);
                }
                case UNSUBSCRIBE -> {
                    String unsubscribeTopic = accessor.getDestination();
                    subscriptionService.removeSubscriber(unsubscribeTopic, sessionId);
                }
                case DISCONNECT -> subscriptionService.removeSession(sessionId);
                case null -> {
                    return message;
                }
                default -> {
                    return message;
                }
            }
        }
        return message;
    }
}
