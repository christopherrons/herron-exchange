package com.herron.exchange.exchangeengine.server.config;

import com.herron.exchange.exchangeengine.server.websocket.ExchangeStompSessionHandler;
import com.herron.exchange.exchangeengine.server.websocket.TradingEventStreamingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

@Configuration
public class WebSocketConfig {

    @Bean
    public TradingEventStreamingService tradingEventStream(SimpMessagingTemplate messagingTemplate) {
        return new TradingEventStreamingService(messagingTemplate);
    }

    //@Bean
    public StompSessionHandler stompSessionHandler() {
        return new ExchangeStompSessionHandler();
    }
}
