package com.herron.exchange.exchangeengine.server.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class WebsocketSessionHandler {
    private final SimpMessagingTemplate messagingTemplate;

    public WebsocketSessionHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
}
