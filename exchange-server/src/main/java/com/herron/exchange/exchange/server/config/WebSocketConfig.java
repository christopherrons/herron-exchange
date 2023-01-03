package com.herron.exchange.exchange.server.config;

import com.herron.exchange.common.api.common.enums.WebSocketTopicEnum;
import com.herron.exchange.exchange.server.websocket.WebSocketDataStreamManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
        registry.addEndpoint("/" + "test").setAllowedOriginPatterns("*").withSockJS();
        registry.addEndpoint("/" + "test").setAllowedOriginPatterns("*");
        registry.addEndpoint("/" + WebSocketTopicEnum.ORDER_STREAM.getTopicName()).setAllowedOriginPatterns("*");
        registry.addEndpoint("/" + WebSocketTopicEnum.ORDER_STREAM.getTopicName()).setAllowedOriginPatterns("*").withSockJS();
        registry.addEndpoint("/" + WebSocketTopicEnum.TRADE_STREAM.getTopicName()).setAllowedOriginPatterns("*");
        registry.addEndpoint("/" + WebSocketTopicEnum.TRADE_STREAM.getTopicName()).setAllowedOriginPatterns("*").withSockJS();
    }

    @Bean
    public WebSocketDataStreamManager webSocketDataStreamHandler(SimpMessagingTemplate messagingTemplate) {
        return new WebSocketDataStreamManager(messagingTemplate);
    }
}