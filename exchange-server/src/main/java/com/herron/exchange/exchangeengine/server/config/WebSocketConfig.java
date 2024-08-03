package com.herron.exchange.exchangeengine.server.config;

import com.herron.exchange.exchangeengine.server.websocket.LiveEventStreamingService;
import com.herron.exchange.exchangeengine.server.websocket.SubscriptionInterceptor;
import com.herron.exchange.exchangeengine.server.websocket.SubscriptionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/exchange").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(subscriptionInterceptor());
    }

    @Bean
    public SubscriptionService subscriptionService() {
        return new SubscriptionService();
    }

    @Bean
    public SubscriptionInterceptor subscriptionInterceptor() {
        return new SubscriptionInterceptor(subscriptionService());
    }

    @Bean
    public LiveEventStreamingService tradingEventStream(SimpMessagingTemplate messagingTemplate) {
        return new LiveEventStreamingService(messagingTemplate, subscriptionService());
    }
}